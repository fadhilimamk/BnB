package main.java;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by fadhil on 05/04/17.
 */
public class SimpleNode {
    int id;
    float cost;
    Integer[][] matrix = null;
    ArrayList<Integer> path = null;
    boolean[] visited = null;

    public SimpleNode() {
        this.id = -1;
        this.cost = -1;
    }

    public SimpleNode(int id, float cost, ArrayList<Integer> path, boolean[]
            visited) {
        this.id = id;
        this.cost = cost;
        this.path = path;
        this.visited = visited;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public Integer[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(Integer[][] matrix) {
        this.matrix = new Integer[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++)
            this.matrix[i] = Arrays.copyOf(matrix[i], matrix.length);
    }

    public ArrayList<Integer> getPath() {
        return path;
    }

    public void setPath(ArrayList<Integer> path) {
        this.path = path;
    }

    public boolean[] getVisited() {
        return visited;
    }

    public void setVisited(boolean[] visited) {
        this.visited = visited;
    }
}
