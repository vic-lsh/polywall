package packetfilter.range;

import java.util.LinkedList;

public class SerialDisjointRange implements DisjointRange {

    private final boolean defaultVal;
    private LinkedList<MutableRange> list;

    public SerialDisjointRange() {
        this.defaultVal = true;
        list = new LinkedList<>();
    }

    public SerialDisjointRange(boolean initialValue) {
        this.defaultVal = initialValue;
        list = new LinkedList<>();
    }

    @Override
    public void set(int min, int max, boolean update) {
        if (max < min) {
            throw new IllegalArgumentException();
        }

        var newNode = new MutableRange(min, max);
        var iter = list.listIterator();

        if (update == defaultVal) {
            while (iter.hasNext()) {
                var node = iter.next();
                switch (node.compareRange(newNode)) {
                    case StrictlyLower:
                        continue;
                    case OverlapLower:
                        node.setMin(max);
                        continue; // newNode may still overlap with other nodes
                    case Contains:
                        // transform from one node to two:
                        // [nodeMin, inputMin] [inputMax, nodeMax]
                        var nodeMax = node.max();
                        node.setMax(min);
                        iter.add(new MutableRange(max, nodeMax));
                        return;
                    case Equal:
                        iter.remove();
                        return;
                    case Contained:
                        iter.remove();
                        continue;
                    case OverlapUpper:
                        node.setMax(min);
                        continue; // newNode may still overlap with other nodes
                    case StrictlyHigher:
                        return;
                }
            }
        } else {
            while (iter.hasNext()) {
                var node = iter.next();
                switch (node.compareRange(newNode)) {
                    case StrictlyLower:
                        continue;
                    case OverlapLower:
                        node.setMin(min);
                        return;
                    case Contains:
                    case Equal:
                        return;
                    case Contained:
                        node.setMin(min);
                        node.setMax(max);
                        return;
                    case OverlapUpper:
                        node.setMax(max);
                        return;
                    case StrictlyHigher:
                        iter.add(newNode); // prepend to this node
                        return;
                }
            }
            // list is empty or all nodes are strictly smaller than myNode
            list.add(newNode);
        }
    }

    @Override
    public boolean get(int x) {
        for (var node : list) {
            if (node.inRange(x)) {
                return !defaultVal;
            }
        }

        return defaultVal;
    }

}
