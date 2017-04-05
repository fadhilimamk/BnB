package main.java;

import java.util.Comparator;

/**
 * Created by fadhil on 05/04/17.
 */
public class NodeComparator implements Comparator<SimpleNode> {
    @Override
    public int compare(SimpleNode node1, SimpleNode node2) {
        if (node1.getCost() < node2.getCost())
            return -1;
        else if (node1.getCost() > node2.getCost())
            return 1;
        else {
            if (node1.getPath().size() > node2.getPath().size())
                return 1;
            else if (node1.getPath().size() < node2.getPath().size())
                return -1;
            else
                return 0;
        }
    }
}
