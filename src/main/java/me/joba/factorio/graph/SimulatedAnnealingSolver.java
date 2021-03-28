package me.joba.factorio.graph;

import java.util.*;

public class SimulatedAnnealingSolver {

    private static final Random RANDOM = new Random(1234); //Repeatable outcomes are nice

//    public static void main(String[] args) {
//        int nodeCount = 1000;
//        Random random = new Random();
//        List<Node> nodes = new ArrayList<>();
//        boolean[][] adjacencyMatrix = new boolean[nodeCount][nodeCount];
//        for(int i = 0; i < nodeCount; i++) {
//            Set<Integer> neighbors = new HashSet<>();
//            for(int x = i + 1; x < nodeCount; x++) {
//                x += random.nextInt(nodeCount / 2);
//                if(x >= nodeCount || random.nextBoolean()) continue;
//                adjacencyMatrix[i][x] = true;
//                adjacencyMatrix[x][i] = true;
//            }
//            for(int n = 0; n < nodeCount; n++) {
//                if(adjacencyMatrix[i][n]) {
//                    neighbors.add(n);
//                }
//            }
//            nodes.add(new Node(i, neighbors));
//        }
//        simulatedAnnealing(nodes, 10000000);
//    }

    public static void simulatedAnnealing(List<Node> nodes, int maxSteps) {
        var matrix = getDistanceMatrix(nodes);
        double currentEnergy = calculateEnergy(matrix);

        printStats(nodes);

        for(int i = 0; i < maxSteps; i++) {
            double temperature = (i + 1.0) / maxSteps;
            int aId = RANDOM.nextInt(nodes.size());
            int bId = RANDOM.nextInt(nodes.size());
            Node a = nodes.get(aId);
            Node b = nodes.get(bId);
            double afterSwap = energyAfterSwap(nodes, currentEnergy, a, b);
            double transitionProbability = getTransitionProbability(currentEnergy, afterSwap, temperature);
            if(RANDOM.nextDouble() < transitionProbability) {
                currentEnergy = afterSwap;
                int tempX = a.getX();
                int tempY = a.getY();
                a.setX(b.getX());
                a.setY(b.getY());
                b.setX(tempX);
                b.setY(tempY);
            }
        }

        printStats(nodes);
    }

    private static double getTransitionProbability(double oldEnergy, double newEnergy, double temperature) {
        if(newEnergy < oldEnergy) return 1;
        return Math.exp(-(newEnergy - oldEnergy)/temperature);
    }

    private static double calculateEnergy(float[][] matrix) {
        double result = 0;
        for(int i = 0; i < matrix.length; i++) {
            float largestDistance = 0;
            for(int j = i + 1; j < matrix.length; j++) {
                float distance = matrix[i][j];
                largestDistance = Math.max(distance, largestDistance);
            }
            result += largestDistance * largestDistance;
        }
        return result;
    }

    private static double energyAfterSwap(List<Node> nodes, double currentEnergy, Node a, Node b) {
        for(int anId : a.getNeighbors()) {
            Node x = nodes.get(anId);
            float energyOldPosition = (float)Math.pow(a.getX() - x.getX(), 2) + (float)Math.pow(a.getY() - x.getY(), 2);
            float energyNewPosition = (float)Math.pow(b.getX() - x.getX(), 2) + (float)Math.pow(b.getY() - x.getY(), 2);
            currentEnergy += distanceToEnergy(energyNewPosition) - distanceToEnergy(energyOldPosition);
        }
        for(int bnId : b.getNeighbors()) {
            Node x = nodes.get(bnId);
            float energyOldPosition = (float)Math.pow(b.getX() - x.getX(), 2) + (float)Math.pow(b.getY() - x.getY(), 2);
            float energyNewPosition = (float)Math.pow(a.getX() - x.getX(), 2) + (float)Math.pow(a.getY() - x.getY(), 2);
            currentEnergy += distanceToEnergy(energyNewPosition) - distanceToEnergy(energyOldPosition);
        }
        return currentEnergy;
    }

    private static double distanceToEnergy(float distance) {
        return distance * distance;
    }

    private static float[][] getDistanceMatrix(List<Node> nodes) {
        float[][] matrix = new float[nodes.size()][nodes.size()];
        for(int i = 0; i < nodes.size(); i++) {
            Node a = nodes.get(i);
            for(int j = i + 1; j < nodes.size(); j++) {
                Node b = nodes.get(j);
                if(a.isNeighbor(b)) {
                    float distance = (float)Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
                    matrix[i][j] = distance;
                }
            }
        }
        return matrix;
    }

    public static void placeInitial(List<Node> nodes) {
        double sqrt = Math.ceil(Math.sqrt((double)nodes.size() / 2));
        int shortEdgeLength = (int)Math.ceil(sqrt);
        int x = 0;
        int y = 0;
        for(Node node : nodes) {
            node.setX(x * 2);
            node.setY(y);
            x++;
            if(x > shortEdgeLength) {
                x = 0;
                y++;
            }
        }
    }

    private static void printStats(List<Node> nodes) {
//        System.out.println("Largest distances");
        double largestTotal = -1;
        double sumTotal = 0;
        for(Node node : nodes) {
            double largestDistance = -1;
            for(int nId : node.getNeighbors()) {
                Node n = nodes.get(nId);
                var distance = Math.sqrt(Math.pow(node.getX() - n.getX(), 2) + Math.pow(node.getY() - n.getY(), 2));
                largestDistance = Math.max(largestDistance, distance);
            }
            largestTotal = Math.max(largestTotal, largestDistance);
            sumTotal += largestDistance;
//            System.out.println("Node " + node.getId() + ": " + largestDistance);
        }
        System.out.println("Largest total: " + largestTotal);
        System.out.println("Sum total: " + sumTotal);

    }
}
