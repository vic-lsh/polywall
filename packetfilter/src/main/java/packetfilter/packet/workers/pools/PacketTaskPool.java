package packetfilter.packet.workers.pools;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.Packet;
import packetfilter.packet.generators.concurrent.EmptyProducerException;
import packetfilter.packet.generators.concurrent.PacketProducer;
import packetfilter.packet.workers.MultiplePacketsWorker;

public class PacketTaskPool extends PacketProcssingPool {
    private Thread workDispatcherThread;
    private PacketProducer producer;
    private ExecutorService executor;
    private int packetsPerTask;
    private FirewallService firewallService;
    private Collection<Future<?>> taskFutures;

    /**
     * Creates a pool for efficient task execution.
     * 
     * User specifies an upper bound to the number of threads used. Thread count
     * could be lowered if using `maxThreadNum` violates the max
     * 256-packet-in-transit constraint (See Little's Law).
     * 
     * @param maxThreadNum maximum number of threads used to execute tasks.
     */
    public PacketTaskPool(int maxThreadNum) {
        super(maxThreadNum);

    }

    /**
     * Configures and specifies dependencies before running the pool.
     * 
     * @param firewallService the firewall service to handle all packets.
     * @param producer        a packet producer instance.
     * @param packetsPerTask  number of packets to be assigned to each task. A task
     *                        is not submitted for execution until this number of
     *                        packets have been generated, or if there's no more
     *                        packets left.
     * @return
     */
    public PacketTaskPool configure(FirewallService firewallService, PacketProducer producer,
            int packetsPerTask) {
        this.producer = producer;
        this.firewallService = firewallService;
        this.executor = Executors.newFixedThreadPool(Math.min(nThreads, (256 / packetsPerTask)));
        this.packetsPerTask = packetsPerTask;

        return this;
    }

    @Override
    public PacketTaskPool start() {
        if (workDispatcherThread != null) {
            throw new IllegalStateException("Cannot invoke 'start' multiple times");
        }

        Collection<Future<?>> tasks = new LinkedList<>();

        workDispatcherThread = new Thread(() -> {
            var pkts = new Packet[this.packetsPerTask];
            int offset = 0;
            while (true) {
                try {
                    var next = producer.poll();
                    if (next == null) {
                        continue;
                    }
                    if (offset <= this.packetsPerTask - 1) {
                        pkts[offset] = next;
                        offset++;
                    } else {
                        var worker = new MultiplePacketsWorker(firewallService, pkts);
                        var handle = this.executor.submit(worker);
                        tasks.add(handle);
                        pkts = new Packet[this.packetsPerTask];
                        offset = 0;
                    }
                } catch (EmptyProducerException e) {
                    if (pkts.length > 0) {
                        tasks.add(this.executor.submit(new MultiplePacketsWorker(firewallService, pkts)));
                    }
                    this.taskFutures = tasks;
                    return;
                }

            }
        });

        workDispatcherThread.start();

        return this;
    }

    @Override
    public PacketTaskPool join() throws InterruptedException, ExecutionException {
        if (workDispatcherThread == null) {
            throw new IllegalStateException("Cannot invoke 'join' before 'start'");
        }

        workDispatcherThread.join();

        for (var future : this.taskFutures) {
            future.get();
        }

        this.executor.shutdown();

        return this;
    }

}