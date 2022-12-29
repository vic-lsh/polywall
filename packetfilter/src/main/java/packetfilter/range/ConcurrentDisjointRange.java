package packetfilter.range;

import java.util.concurrent.ConcurrentSkipListSet;

public class ConcurrentDisjointRange implements DisjointRange {

    private final boolean defaultVal;

    // Maintains a set of sorted nodes, indicating the range(s) not permitted.
    private ConcurrentSkipListSet<ImmutableRange> constraints;

    public ConcurrentDisjointRange() {
        this(true);
    }

    public ConcurrentDisjointRange(boolean defaultVal) {
        this.defaultVal = defaultVal;
        this.constraints = new ConcurrentSkipListSet<>();
    }

    ConcurrentDisjointRange(boolean defaultVal, ConcurrentSkipListSet<ImmutableRange> constraints) {
        this.defaultVal = defaultVal;
        this.constraints = constraints;
    }

    @Override
    public ConcurrentDisjointRange clone() {
        var clonedConstraints = this.constraints.clone();
        return new ConcurrentDisjointRange(defaultVal, clonedConstraints);
    }

    @Override
    public void set(int min, int max, boolean update) throws IllegalArgumentException {
        if (max < min) {
            throw new IllegalArgumentException();
        }

        if (update == this.defaultVal) {
            this.removeRestriction(min, max);
        } else {
            this.addRestriction(min, max);
        }
    }

    @Override
    public boolean get(int x) {
        var target = new ImmutableRange(x, x);

        var floor = this.constraints.floor(target);
        if (floor != null) {
            if (floor.max() >= x) {
                return !defaultVal;
            }
        }

        var ceil = this.constraints.ceiling(target);
        if (ceil != null) {
            if (ceil.min() > x) {
                return defaultVal;
            } else { // ceil.min == x
                return !defaultVal;
            }
        } else {
            return defaultVal;
        }
    }

    private void addRestriction(int min, int max) {
        this.constraints.add(new ImmutableRange(min, max));
    }

    private void removeRestriction(int min, int max) {
        var target = new ImmutableRange(min, max);

        if (this.constraints.remove(target)) {
            return;
        } else {
            while (true) {
                if (this.constraints.isEmpty()) {
                    return;
                }

                Boolean noOverlap = true;

                nodeIter: for (var node : this.constraints) {
                    nodeCompare: switch (node.compareRange(target)) {
                        case StrictlyLower: {
                            break nodeCompare;
                        }
                        case OverlapLower: {
                            noOverlap = false;
                            this.tryAdd(max + 1, node.max());
                            this.constraints.remove(node);
                            break nodeIter;
                        }
                        case Contains: {
                            noOverlap = false;
                            this.tryAdd(node.min(), min - 1);
                            this.tryAdd(max + 1, node.max());
                            this.constraints.remove(node);
                            break nodeIter;
                        }
                        case Contained:
                        case Equal: {
                            noOverlap = false;
                            this.constraints.remove(node);
                            break nodeIter;
                        }
                        case OverlapUpper: {
                            noOverlap = false;
                            this.tryAdd(node.min(), min - 1);
                            this.constraints.remove(node);
                            break nodeIter;
                        }
                        case StrictlyHigher: {
                            return;
                        }
                    }
                }

                if (noOverlap) {
                    return;
                }
            }
        }
    }

    /**
     * Add range to the internal set if min is not greater than max.
     */
    private boolean tryAdd(int min, int max) {
        boolean add = min <= max;
        if (add) {
            this.constraints.add(new ImmutableRange(min, max));
        }
        return add;
    }
}
