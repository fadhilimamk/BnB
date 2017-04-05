package main.java;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.applet.Applet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class TSPSolver extends Applet{

    private static int INF = Integer.MAX_VALUE;
    private static int REDUCED_COST_MATRIX_METHOD = 1;
    private static int BOBOT_TUR_LENGKAP_METHOD = 2;
    private static int CHOOSEN_METHOD = REDUCED_COST_MATRIX_METHOD;
    private static Integer[][] adjajency;
    private static int NUMBER_OF_NODE = 0;

    public static void main (String args[]) {
        new TSPSolver();
    }

    public TSPSolver() {
        System.out.println("Solving TSP using method no : "+CHOOSEN_METHOD);

        // Reading file
        MapReader reader = new MapReader("map2.json");
        adjajency = reader.getMap();
        NUMBER_OF_NODE = adjajency.length;

        // Displaying map to solve
        Graph graph = new SingleGraph("Map of Node");
        displayMap(graph);

        SimpleNode finalResult = null;
        if (CHOOSEN_METHOD == REDUCED_COST_MATRIX_METHOD){
            finalResult = initTSPWithReducedCostMatrix();
        } else if (CHOOSEN_METHOD == BOBOT_TUR_LENGKAP_METHOD){
            finalResult = initTSPWIthBobotTurLengkap();
        }

        // Drawing path
        if (finalResult != null)
            drawRoute(finalResult.getPath(), graph);

    }

    private void displayMap(Graph graph){
        graph.setStrict(false);
        graph.setAutoCreate(true);
        graph.display();

        boolean showArrow = (CHOOSEN_METHOD == REDUCED_COST_MATRIX_METHOD);

        for(int i = 0; i < NUMBER_OF_NODE; i++){
            for(int j = 0; j < NUMBER_OF_NODE; j++){
                if (adjajency[i][j] != INF){
                    graph.addEdge(Integer.toString(i)+Integer.toString(j),
                            Integer.toString(i), Integer.toString(j), showArrow);
                    try {
                        graph.getEdge(Integer.toString(i)+Integer.toString(j))
                                .setAttribute("ui.label",adjajency[i][j]);
                    } catch (Exception e) {

                    }
                }
            }
        }

        graph.getNode(Integer.toString(0)).setAttribute("ui.class","marked");

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }

        graph.addAttribute("ui.stylesheet", styleSheet);
    }

    private void drawRoute(ArrayList<Integer> path, Graph graph) {
        for (int i = 0; i < path.size()-1; i++){
            Edge cEdge = graph.getEdge(Integer.toString(path
            .get(i))+Integer.toString(path.get(i+1)));
            try {
                cEdge.setAttribute("ui.class","selected");
            } catch (Exception e) {
                cEdge = graph.getEdge(Integer.toString(path
                        .get(i+1))+Integer.toString(path.get(i)));
                cEdge.setAttribute("ui.class","selected");
            }
        }
        Edge cEdge = graph.getEdge(Integer.toString(path
                .get(path.size()-1))+Integer.toString(0));
        try {
            cEdge.setAttribute("ui.class","selected");
        } catch (Exception e) {
            cEdge = graph.getEdge(Integer.toString(0)+Integer.toString(path
                    .get(path.size()-1)));
            cEdge.setAttribute("ui.class","selected");
        }
    }



//    ---------------------------------------------------------------------------
//    BOBOT TUR LENGKAP
//    ---------------------------------------------------------------------------


    private SimpleNode initTSPWIthBobotTurLengkap() {
        // Preparing some variabel to solving the problem
        NodeComparator comparator = new NodeComparator();
        PriorityQueue<SimpleNode> lifeNode = new PriorityQueue<>(1,
                comparator);
        ArrayList<Integer> path = new ArrayList<>();
        int counter = 0;
        float finalCost = INF;
        boolean finish = false;
        boolean[] visited = new boolean[NUMBER_OF_NODE];
        for (int i = 0; i < NUMBER_OF_NODE; i++) {
            visited[i] = false;
        }

        Graph dGraph = new SingleGraph("Dynamic Graph");
        dGraph.addAttribute("ui.stylesheet", styleSheet);
        dGraph.setStrict(false);
        dGraph.setAutoCreate(true);
        dGraph.display();

        PriorityQueue<SimpleNode> solution = new PriorityQueue<>(1,comparator);

        // Preparing first node in graph
        path.add(0);
        float cost = getNodeCostBT(path);
        visited[0] = true;
        SimpleNode currentNode = new SimpleNode(counter,cost,path, visited);
        dGraph.addNode("0");
        dGraph.getNode("0").addAttribute("ui.label", "0 - ("+currentNode.getCost()
                +")");
        dGraph.getNode("0").setAttribute("ui.class","marked");

        // Start BnB
        long tStart = System.currentTimeMillis();
        lifeNode.add(currentNode);
        while (!lifeNode.isEmpty() && !finish) {
            currentNode = lifeNode.poll();

            if (currentNode.getCost() > finalCost) {
                finish = true;
            }

            for (int i = 0; i < NUMBER_OF_NODE && !finish; i++) {

                if (isNextPathAvailable(currentNode.getPath(),i) && !currentNode.getVisited()[i]){
                    counter++;
                    path = new ArrayList<>(currentNode.getPath().size()+1);
                    path.addAll(currentNode.getPath());
                    path.add(i);
                    visited = Arrays.copyOf(currentNode.getVisited(),
                            NUMBER_OF_NODE);
                    visited[i] = true;
                    SimpleNode childNode = new SimpleNode(
                            counter,
                            getNodeCostBT(path),
                            path,
                            visited);

                    lifeNode.add(childNode);

                    dGraph.addEdge(Integer.toString(currentNode.getId())
                            +Integer.toString(counter), Integer.toString
                            (currentNode.getId()), Integer.toString(counter));
                    Node cNode = dGraph.getNode(Integer.toString(counter));
                    cNode.setAttribute("ui.label", counter+" - ("+childNode.getCost
                                    ()+")");

                    if (isSolutionNode(childNode)) {
                        cNode.setAttribute("ui.class","solution");
                        solution.add(childNode);
                        if (childNode.getCost() < finalCost)
                            finalCost = childNode.getCost();
                    }

                }
            }
        }
        long tEnd = System.currentTimeMillis();

        System.out.println("Number of solution : " + solution.size());

        System.out.println("One of the best solution :");
        SimpleNode finalresult = solution.poll();
        for (int j = 0; j < finalresult.getPath().size(); j++)
            System.out.print(finalresult.getPath().get(j)+1 + " ");
        System.out.print("1 ");
        System.out.println(" | Cost : >" + finalresult.getCost() + " | Real " +
                "Cost :" + calculateRealCost(finalresult) +
                " | Number of Node : "+counter);

        System.out.println("Elapsed time : " + (tEnd - tStart)/1000.0 +
                "seconds");

        dGraph.getNode(Integer.toString(finalresult.getId()))
                .setAttribute("ui" +
                        ".class",
                "final");

        return finalresult;

    }


    private boolean isNextPathAvailable(ArrayList<Integer> path, int nodeId) {
        return adjajency[path.get(path.size()-1)][nodeId] != INF;
    }

    private boolean isSolutionNode(SimpleNode node) {
        return node.getPath().size() == NUMBER_OF_NODE;
    }

    private float calculateRealCost(SimpleNode node){
        float cost = 0;
        ArrayList<Integer> path = node.getPath();

        for (int i = 0; i < path.size()-1; i++)
            cost += adjajency[path.get(i)][path.get(i+1)];
        cost += adjajency[path.get(path.size()-1)][0];

        return cost;
    }

    private float getNodeCostBT(ArrayList<Integer> path) {
        // Preparing
        float cost = 0;
        boolean[][] wajib = new boolean[NUMBER_OF_NODE][NUMBER_OF_NODE];
        for (int i = 0; i < NUMBER_OF_NODE; i++)
            for (int j = 0; j < NUMBER_OF_NODE; j++)
                wajib[i][j] = false;

        if (path.size() > 1) {
            System.out.print("GetCost untuk path :");
            for (int i = 0; i < path.size()-1; i++){
                wajib[path.get(i)][path.get(i+1)] = true;
                wajib[path.get(i+1)][path.get(i)] = true;
                System.out.print(path.get(i) + " ");
            }
            System.out.print(path.get(path.size()-1));
            System.out.println();
        }

        for (int i = 0; i < NUMBER_OF_NODE; i++) {
            // Get 2 minimum edges from available edge
            int min1, min2;
            if (adjajency[i][0] < adjajency[i][1]) {
                min1 = adjajency[i][0];
                min2 = adjajency[i][1];
            } else {
                min1 = adjajency[i][1];
                min2 = adjajency[i][0];
            }
            ArrayList<Integer> candidate = new ArrayList<>(2);
            if (wajib[i][0])
                candidate.add(adjajency[i][0]);
            if (wajib[i][1])
                candidate.add(adjajency[i][1]);
            for (int j = 2; j < NUMBER_OF_NODE; j++) {
                if (wajib[i][j])
                    candidate.add(adjajency[i][j]);
                if (adjajency[i][j] < min1) {
                    if (min2 > min1)
                        min2 = min1;
                    min1 = adjajency[i][j];
                } else if (adjajency[i][j] < min2)
                    min2 = adjajency[i][j];
            }

            if (candidate.size() == 1) {
                if (candidate.get(0) != min1)
                    min2 = candidate.remove(0);
            } else if (candidate.size() == 2) {
                min1 = candidate.remove(0);
                min2 = candidate.remove(0);
            }

            System.out.print(" ("+min1 +" + "+ min2+") ");
            cost += min1 + min2;
        }

        System.out.println(" = "+cost);

        return cost/2;
    }


//    ---------------------------------------------------------------------------
//    REDUCED COST MATRIX
//    ---------------------------------------------------------------------------

    private SimpleNode initTSPWithReducedCostMatrix() {
        // Preparing some variabels to solving the problem
        NodeComparator comparator = new NodeComparator();
        PriorityQueue<SimpleNode> lifeNode = new PriorityQueue<>(1,
                comparator);
        ArrayList<Integer> path = new ArrayList<>();
        int counter = 0;
        float finalCost = INF;
        boolean finish = false;
        boolean[] visited = new boolean[NUMBER_OF_NODE];
        for (int i = 0; i < NUMBER_OF_NODE; i++) {
            visited[i] = false;
        }

        Graph dGraph = new SingleGraph("Dynamic Graph");
        dGraph.addAttribute("ui.stylesheet", styleSheet);
        dGraph.setStrict(false);
        dGraph.setAutoCreate(true);
        dGraph.display();

        PriorityQueue<SimpleNode> solution = new PriorityQueue<>(1,comparator);

        // Preparing first node in graph
        path.add(0);
        visited[0] = true;
        ReducedCostMatrix firstRCM = getReducedCostMatrix(adjajency,null,path);
        float cost = firstRCM.getCost();
        adjajency = firstRCM.getMatrix();
        SimpleNode currentNode = new SimpleNode(counter,cost,path, visited);
        currentNode.setMatrix(adjajency);
        dGraph.addNode("0");
        dGraph.getNode("0").addAttribute("ui.label", "0 - ("+currentNode.getCost()
                +")");
        dGraph.getNode("0").setAttribute("ui.class","marked");

        // Start BnB
        long tStart = System.currentTimeMillis();
        lifeNode.add(currentNode);
        while (!lifeNode.isEmpty() && !finish) {
            currentNode = lifeNode.poll();

            if (currentNode.getCost() > finalCost) {
                finish = true;
            }

            for (int i = 0; i < NUMBER_OF_NODE && !finish; i++) {

                if (isNextPathAvailable(currentNode.getPath(),i) && !currentNode.getVisited()[i]){
                    counter++;
                    path = new ArrayList<>(currentNode.getPath().size()+1);
                    path.addAll(currentNode.getPath());
                    path.add(i);
                    visited = Arrays.copyOf(currentNode.getVisited(),
                            NUMBER_OF_NODE);
                    visited[i] = true;

                    ReducedCostMatrix childRCM = getReducedCostMatrix(currentNode
                            .getMatrix(),currentNode.getCost(),path);
                    SimpleNode childNode = new SimpleNode(
                            counter,
                            childRCM.getCost(),
                            path,
                            visited);
                    childNode.setMatrix(childRCM.getMatrix());

                    lifeNode.add(childNode);

                    dGraph.addEdge(Integer.toString(currentNode.getId())
                            +Integer.toString(counter), Integer.toString
                            (currentNode.getId()), Integer.toString(counter));
                    Node cNode = dGraph.getNode(Integer.toString(counter));
                    cNode.setAttribute("ui.label", counter+" - ("+childNode.getCost
                            ()+")");



                    if (isSolutionNode(childNode)) {
                        cNode.setAttribute("ui.class","solution");
                        solution.add(childNode);
                        if (childNode.getCost() < finalCost)
                            finalCost = childNode.getCost();
                    }

                }
            }

        }

        long tEnd = System.currentTimeMillis();

        System.out.println("Number of solution : " + solution.size());

        System.out.println("One of the best solution :");
        SimpleNode finalresult = solution.poll();
        for (int j = 0; j < finalresult.getPath().size(); j++)
            System.out.print(finalresult.getPath().get(j) + " ");
        System.out.println(" | Cost : " + finalresult.getCost() + " | " +
                "Number of Node : "+counter);

        System.out.println("Elapsed time : " + (tEnd - tStart)/1000.0 +
                " seconds");

        dGraph.getNode(Integer.toString(finalresult.getId()))
                .setAttribute("ui" +
                                ".class",
                        "final");

        return finalresult;
    }

    private ReducedCostMatrix getReducedCostMatrix(final Integer[][] prevMatrix,
                                                   Float pCost,
                                                   ArrayList<Integer> path){

        // menyalin reduced cost matrix sebelumnya ke matrix lokal
        Integer[][] matrix = new Integer[NUMBER_OF_NODE][NUMBER_OF_NODE];
        for (int i = 0; i < NUMBER_OF_NODE; i++){
            System.arraycopy(prevMatrix[i], 0, matrix[i], 0, NUMBER_OF_NODE);
        }

        int row = 0;
        int col = 0;

        // membuat infinite 2 path terakhir
        if(path.size() > 1) {
            row = path.get(path.size()-2);
            col = path.get(path.size()-1);
            for (int i = 0; i < NUMBER_OF_NODE; i++){
                matrix[row][i] = INF;
            }
            for (int i = 0; i < NUMBER_OF_NODE; i++){
                matrix[i][col] = INF;
            }
            matrix[col][0] = INF;
        }

        float cost = (pCost == null)? 0 : pCost;
        // menghitung cost dari reduksi baris
        for (int i = 0; i < NUMBER_OF_NODE; i++){
            int min = INF;
            for (int j = 0; j < NUMBER_OF_NODE; j++){
                if (matrix[i][j] < min)
                    min = matrix[i][j];
            }
            if (min != INF && min != 0){
                for (int j = 0; j < NUMBER_OF_NODE; j++){
                    if (matrix[i][j] != INF)
                        matrix[i][j]-= min;
                }
                cost += min;
            }
        }
        // menghitung cost dari reduksi kolom
        for (int i = 0; i < NUMBER_OF_NODE; i++){
            int min = INF;
            for (int j = 0; j < NUMBER_OF_NODE; j++){
                if (matrix[j][i] < min)
                    min = matrix[j][i];
            }
            if (min != INF && min != 0){
                for (int j = 0; j < NUMBER_OF_NODE; j++){
                    if (matrix[j][i] != INF)
                        matrix[j][i] -= min;
                }
                cost += min;
            }
        }

        if (path.size() > 1)
            cost += adjajency[row][col];

        ReducedCostMatrix reducedCostMatrix = new ReducedCostMatrix();
        reducedCostMatrix.setMatrix(matrix);
        reducedCostMatrix.setCost(cost);


        // Printing some data
        System.out.print("Menghasilkan matriks untuk path:");
        for (Integer cPath : path) {
            System.out.print(cPath + " ");
        }
        System.out.println();
        for(int i = 0; i < NUMBER_OF_NODE; i++){
            for(int j = 0; j < NUMBER_OF_NODE; j++){
                if (matrix[i][j] != INF)
                    System.out.print(matrix[i][j]+"\t");
                else
                    System.out.print("∞\t");
            }
            System.out.println();
        }
        System.out.println("cost:" + cost);
        System.out.println();
        System.out.println();

        return reducedCostMatrix;
    }

    private String styleSheet =
            "node {" +
            "       fill-color: black;" +
            "}" +
            "node.marked {" +
            "	fill-color: red;" +
            "}" +
            "node.solution {" +
            "	fill-color: green;" +
            "}" +
            "node.final {" +
            "	fill-color: blue;" +
            "}" +
            "edge.selected {" +
            "	fill-color: red;" +
            "	stroke-width: 3;" +
            "	z-index: 10;" +
            "}";

}
