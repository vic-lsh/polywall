# CAPSTONE Architecture Proposal

This design proposal first establishes a baseline, serial implementation approach. Then, it presents a preliminary multithreaded design. The third, final section discusses workloads where the multithreaded design would perform worse, and offers alternative design approaches.

## 1. Serial implementation

Here, we present an overview of a possible serial implementation. It provides a performance baseline against which we benchmark our future, concurrent firewall.

### 1.1 High-level implementation

The serial implementation follows the pseudocode below:

```
fun firewall():
	while p = PacketGenerator.getPacket():
		if p.isDataPacket():
			if (not PNG[p.src]) and R[p.dst].has(p.src):
				histogram.add(p.fingerprint())
		else: // p is a config packet
			// update PNG mapping
			PNG[p.addr] = p.personaNonGrata
			// update the accepting addr. set for `p.addr`
			R[p.addr].update(p.addrBegin, p.addrEnd, p.acceptRange)
```

For each packet, we first determine whether it is a data or configuration packet. If it is a data packet, we perform access control check on this packet. For each data packet from `src` to `dst`, the firewall permits this packet if and only if $PNG[src] = false AND src \in R[dst]$. Alternatively, the packet is a configuration packet. The firewall updates the `PNG` and `R` structures according to the payload of this configuration packet.

This implementation follows the spec because:

1. A data packet's fingerprint is never calculated if it does not pass access control
2. A configuration packet's payload leads to immediate updates to `PNG` and `R`, so the next data packet will read the updated access control constraints
3. Packets are processed sequentially, so no more than 256 packets are in flight at the same time

### 1.2 Implementation of key data structures

The `PNG` data structure is implemented as a hash table, where the key is an address, and the value is a boolean. Java's `HashMap` is a suitable choice because our use case does not have any special requirements.

The `R` data structure is implemented as a hash table, where the key is an address, and the value is a set of addresses. This enables the firewall to efficiently lookup whether a source address is within the set of permitted send addresses of a particular desintation address. Java's `HashMap` and and `HashSet` provides functionalities that closely tie to our requirements.

## 2. Preliminary High-Performance Design

Below we sketch an initial high-performance design and briefly justify its correctness.

This design makes the following assumptions, which the author believes to be reasonable:

1. There are a lot more data packets than config packets
   - This is inspired by the difference in data vs. control plane throughput in networking.
2. Trains exhibit temporal locality
   - If there is a train of packets from A to B, it's likely that there is another train from A to B in the near future.
   - This mimics communication in the real world, where communication with another party is usually followed by follow-up messages.

Section 3 challenges these assumptions and discusses alternative designs that may perform better under different assumptions.

For notational clarity, we use `Address` as an alias type for the 4-byte `int` in implementation.

### 2.1 Packet dispatch

A master thread holds the packet generator. Two types of workers -- data worker and config worker -- are responsible for data and configuration packets, respectively. When a worker finishes a packet, it joins its waiting pool. There are two waiting pools, one for data workers and the other for config workers. The master thread progressively gives new packets to idle workers, or waits if no such idle worker exists. Once an idle worker receives a packet, it processes it and comes back to the waiting pool.

The master thread can infer the number of packets in flight. Assume the master thread knows there are $C$ config workers and $D$ data workers. Because each worker processes one packet at a time, the maximum number of packets in flight is $C+D$. If $C + D < 256$, the master thread can release new packets as idle workers show up. Otherwise, the total number of packets in flight is $C + D - I_C - I_D$, where $I_C$ and $I_D$ are the number of idle data and config workers, respectively. As long as the master thread maintains invariant $C + D - I_C - I_D \leq 256$, no more than 256 packets can be in flight simultaneously.

### 2.2 Packet processing -- data packets

When processing a data packet, a data worker has two responsibilities:

1. It must check whether a data packet passes access control.
2. If this data packet passes access control, it must collect the packet's fingerprint.

For access control, we create an abstraction named `ACL` (Access Control Lookup) (discussed further in Section 2.4). `ACL` includes a caching layer for computed access control results. If `ACL` fails to find the desired cache entry, it falls back to accessing `PNG` and `R`, then caches this result for future access.

`ACL` implements the interface below:

```
interface ACLReader {
	int get(Address src, Address dst);
	int getWithRefId(RefId id, Address src, Address dst);
}
```

This interface leverages the observation that if `PNG` and `R` do not change, access control result does not change for a source-destination pair.

When a worker encounters a new train (i.e. a new source-destination pair), it calls `get()` to determine this packet's access control. The `int` returned by `get()` has two pieces of information:

1. this packet's access control (`True` or `False`), and 
2. a `RefId` to make future accesses for this source-destination pair more efficient

The access control result is stored in the highest bit of the returned int: if this is 1, the packet is permitted, or else it needs to be dropped immediately. The rest of the integer, after setting the highest bit to 0, is the `RefId`.

A `RefId` is an index to a cache array. It is used for efficient cache lookup.

The data worker keeps track of the source, destination, and `RefId`s of its previous N packet. If the next packet has a source-destination pair it recognizes, the worker calls `getWithRefId()` instead of `get()`.

The number returned from `getWithRefId()` has the same content structure that from `get()`. Critically, the `RefId` contained in this method may differ from the `RefId` it passed to `ACL`. This is because the cache entry stored in this `RefId` may be outdated. In such a case, `ACL` recomputes the access control result, assigns a new cache slot for this address pair, then returns the new `RefId` and the result as an `int`.

After access control and filtering, the worker calculates a packet's fingerprint. The fingerprint result is enqueued to a queue. The data worker then drops this packet and returns to the data worker waiting pool. A special, histogram worker is responsible for dequeueing packet fingerprints and placing them into its corresponding bin, thereby maintaining a histogram.

### 2.3 Packet processing -- configuration packets

When processing a configuration packet, the config worker needs to update two entries, one in `PNG` and the other in `R`. It must also revoke relevant cache entries. The order in which these operations happen must be carefully arranged to ensure serializability.

`PNG` and `R` are implemented as concurrent hash tables. There is a `RWLock` before each bucket in each hash table. A config worker first determines the bucket it needs to access by calculating the hash of $address$ in the config packet. It then acquires the lock for the entry to modify in `PNG`, then for `R`. The locking order can be flipped, so long as every config worker follows the same order.

Once the worker acquires both locks, it has exclusive access to these two buckets. Importantly, data and config workers that require access to other buckets do not need to wait for this config worker (though they may wait for other config workers).

With these two locks, the config worker begins invalidating every relevant cache entry. The cache entry is relevant if either its source or destination address is in: $$\{Address\} \cup \{x | x \geq addressBegin, x \leq addressEnd \}$$ Invalidation sets each cache entry to an invalid value (e.g. `null` or `-1`). The section on `ACL` below details how to search for such entries correctly and efficiently.

For data workers querying access control for affected address pairs, its call to `ACL` either encounters an invalidated cache entry, or one that has not been invalidated yet. If a valid cache entry is found, the worker is allowed to continue processing. These workers are oblivious to the existence of this config worker; such data packets are serialized before this config packet. If an invalidated cache entry is encountered, a data worker needs to wait until this config worker releases both locks. Locks are released only after `PNG` and `R` are updated, therefore these workers' packets are serialized after the config packet.

After the config worker invalidates all relevant cache entries, it updates `PNG` and `R`. Because no other thread has access to the buckets to be modified, modification is trivial. After modifications, the config worker releases locks in the order in which it acquired them.

### 2.4 The `ACL`

The `ACL` maintains an abstraction for both data workers (the readers) and config workers (the writers). Internally, it is comprised of `PNG`, `R`, as well as an array of caches:

```
class ACL {
	ConcurrentHashMap<Address, Bool> PNG;
	ConcurrentHashMap<Address, Set<Address>> R;
	CacheBucket[] cache;
}
```

where `cache` is an array of smaller caches. Each `CacheBucket` is responsible for caching `1 / cache.length` of the address space.

The abstraction for data workers is listed in the interface `ACLReader` in Section 2.2. On the worker's side, `ACL` also implements an interface:

```
interface ACLWriter {
	void configUpdate(Packet p);
}
```

`configUpdate()` only accepts configuration packets. Calling it with a data packet causes an exception.

`ACL`'s goal is to provide a fast path for workers working on a specific train. After the first cache miss, the worker will be able to get access control result from one array access by indexing into `cache`, until a config worker invalidates that cache.

#### Query from data workers

First, let's consider `get(Address src, Address dst)`:

```
int get(Address src, Address dst) {
	int bucket = calcBucket(src);
	RefId refid = cache[bucket].getNextRefId();
	bool result = !PNG.get(src) && R.get(dst).contains(src);
	cache[bucket][refid].set(packAsLong(result, src, dst));
	return packAsInt(result, refid);
}

// Relevant caching class
class CacheBucket {
	AtomicLong[] bucket;
	AtomicLong refIdCtr;
	// When invalidating a cache entry, push the invalidated
	// ref ID to this queue for future re-use
	ConcurrentQueue staleRefIds;

	RefId getNextFreeRefId() {
	try {
		return staleRefIds.pop();
	} catch (EmptyException e) {
		return (refIdCtr.fetchAndIncrement() % cache.length);
	}
}

}
```

`get()` first reserves a `RefId`, either by reusing a `RefId` associated with a stale cache entry, or getting one slot from the `refIdCtr`. It then adds the `RefId` to the `cacheSearch` hash table, in ascending address order. The procedure calculates access control permission, then packs this result, as well as the source-destination pair, into one long. Finally, it returns both the access control result and the `RefId`.

```
int getByRefId(RefId refid, Address src, Address dst) {
	int bucket = calcBucket(src);
	int result, cachedSrc, cachedDst;
	(result, cachedSrc, cachedDst) = unpack(cache[bucket].load(refid));
	if (cachedSrc == src && cachedDst == dst && result != -1) {
		return result;
	} else { // refID out of date
		return get(src, dst);  // recompute and cache results
	}
}
```

`getByRefId()` first attempts the fast path. It checks whether the value stored at the `refid` is still valid; that is, the value is still for the targeted source-destination pair, and the stored value has not been invalidated. If this is the case, we return the result. Otherwise, it fall backs to the `get()` routine above.

#### Update from config worker

```
void configUpdate(Packet p) {
	if (p.type == DataPacket) {
		throw new WrongPacketTypeException();
	}

	int bucket = calcBucketForPNGandR(p.address);
	PNG.lockBucket(bucket);
	try {
		R.lockBucket(bucket);
		try {
			var sortedAddrs = [p.addressBegin .. p.addressEnd]
				.insertInOrder(p.address);
			var invalidCacheBuckets = findCacheBuckets(sortedAddrs);
			for (cacheBucket : invalidCacheBuckets) {
				cacheBucket.invalidateAll();
			}
			PNG.unsafeSet(p.address, p.personNonGrata);
			R.unsafeGet(p.address)
				.update(p.addressBegin, p.addressEnd, p.acceptingRange);
		} finally {
			R.unlockBucket(bucket);
		}
	} finally {
		PNG.unlockBucket(bucket);
	}
}
```

This method first locks the buckets that need to be modified in `PNG` and `R`. `PNG` and `R`'s buckets are protected by RWLocks, used to provide a config worker exclusive access to buckets. Once locked, the config worker finds all cache buckets it needs to invalidate. Cache buckets are arranged in ascending, sender address order. If the max address is S, and there are N `CacheBucket`s, then the first bucket contains all cache entries for source-destination pairs where the source is in $[0 .. S / N)$. The next cache bucket contains cache entries where the source is in $[S / N, 2S / N)$, and so forth. All entries affected by the `PNG` update is invalidated, because `PNG` is indexed by source addresses and `p.address` is in mapped to one of the `invalidCacheBuckets`. Similarly, all entries affected by the `R` update is invalidated, because `p.addressBegin` and `p.addressEnd` refer to sender addresses, and all associated cache buckets are in `invalidCacheBuckets`.

Once all relevant cache entries are invalidated, the config worker updates `PNG` and `R`. The methods are prefixed with "unsafe" because they assume the caller has already acquired relevant locks.

## 3 Design Discussion

This design would work better for packet streams with a relatively small number of source-destination pairs, and a relatively low fraction of config packets.

Much of the design revolves around a caching fast path. The fast path works if a worker has encountered a source-destination pair before, and the access control result has not been invalidated.

This design could be simplified by removing the caching layer. Without cache, an access control would necessitate two hash table reads. Reading from `R` also imposes another find-in-set operation. Having multiple dereferencing in a single access control check is not cache-friendly. To compare, a cache implemented as a contiguous array can be brought into the CPU cache for repeated access. However, repeatedly accessing one cache array also assumes a high probability of a worker processing the same source-destination pairs, which may not be the case.

This design could also be simplified by removing worker specialization. Instead of having data and config workers, use one type of worker to process any packet. With this approach, and with caching removed, the concurrent design could be dramatically simplified, making it easier to reason about.

Lastly, this design makes an effort to avoid a "stop-the-world" scenario when a config packet brings the entire firewall to a stop. It is unclear if there is a design that allows config packets can be updated without locking. Such a design should work better if the portion of config packets is high, and therefore causes high contention on the locks of `PNG` and `R`.