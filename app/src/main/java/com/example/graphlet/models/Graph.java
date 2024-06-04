package com.example.graphlet.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Graph implements Serializable {
    public int id;
    public String name;
    public GraphType type;
    public List<Node> nodes;
    public List<Edge> edges;

    public Graph(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = GraphType.values()[type];
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public Graph(int id, String name, GraphType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public Graph(String name, GraphType type) {
        this.id = NO_ID_PRESENT;
        this.name = name;
        this.type = type;
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public enum GraphType {
        DIJKSTRA_UNDIRECTED,
        DIJKSTRA_DIRECTED
    }

    public static class Node implements Serializable {
        public int id;
        public float x;
        public float y;
        public boolean isSelected;

        public Node(int id, float x, float y) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.isSelected = false;
        }
    }

    public static class Edge implements Serializable {
        public int nodeId1;
        public int nodeId2;
        public double weight;
        public boolean isDirected;
        public boolean isSelected;

        public Edge(int nodeId1, int nodeId2, boolean isDirected) {
            this.nodeId1 = nodeId1;
            this.nodeId2 = nodeId2;
            this.weight = 0.0f;
            this.isDirected = isDirected;
            this.isSelected = false;
        }

        public Edge(int nodeId1, int nodeId2, double weight, boolean isDirected) {
            this.nodeId1 = nodeId1;
            this.nodeId2 = nodeId2;
            this.weight = weight;
            this.isDirected = isDirected;
            this.isSelected = false;
        }
    }

    public List<AlgorithmStep> performDijkstraWithSteps(int startNodeId, int endNodeId) {
        List<AlgorithmStep> steps = new ArrayList<>();

        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> previousNodes = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(NodeDistance::getDistance));

        for (Node node : nodes) {
            distances.put(node.id, Double.MAX_VALUE);
            previousNodes.put(node.id, -1);
        }
        distances.put(startNodeId, 0.0);
        pq.add(new NodeDistance(startNodeId, 0.0));

        int stepNumber = 0;
        String explanation = "Initializing distances. Setting all distances to infinity except the start node.";
        steps.add(new AlgorithmStep(startNodeId, distances, previousNodes, stepNumber, explanation));
        stepNumber++;

        boolean reachedEndNode = false;

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            int currentNodeId = current.nodeId;

            // Explanation for processing the current node
            explanation = "Processing node " + currentNodeId + ".";
            steps.add(new AlgorithmStep(currentNodeId, distances, previousNodes, stepNumber, explanation));
            stepNumber++;

            if (currentNodeId == endNodeId) {
                explanation = "Reached the end node " + endNodeId + ".";
                steps.add(new AlgorithmStep(currentNodeId, distances, previousNodes, stepNumber, explanation));
                reachedEndNode = true;
                break;
            }

            for (Edge edge : edges) {
                if ((type == GraphType.DIJKSTRA_UNDIRECTED && (edge.nodeId1 == currentNodeId || edge.nodeId2 == currentNodeId)) ||
                        (type == GraphType.DIJKSTRA_DIRECTED && edge.nodeId1 == currentNodeId)) {
                    int neighborId = (edge.nodeId1 == currentNodeId) ? edge.nodeId2 : edge.nodeId1;
                    double newDist = distances.get(currentNodeId) + edge.weight;

                    if (newDist < distances.get(neighborId)) {
                        distances.put(neighborId, newDist);
                        previousNodes.put(neighborId, currentNodeId);
                        pq.add(new NodeDistance(neighborId, newDist));

                        // Explanation for updating the distance
                        explanation = "Relaxing edge (" + currentNodeId + ", " + neighborId + "). ";
                        explanation += "Updating distance of node " + neighborId + " to " + newDist + ".";
                        steps.add(new AlgorithmStep(currentNodeId, distances, previousNodes, stepNumber, explanation));
                        stepNumber++;
                    }
                }
            }
        }

        if (!reachedEndNode) {
            explanation = "No path found from node " + startNodeId + " to node " + endNodeId + ".";
            steps.add(new AlgorithmStep(endNodeId, distances, previousNodes, stepNumber, explanation));
        } else {
            explanation = "Final distances computed.";
            steps.add(new AlgorithmStep(endNodeId, distances, previousNodes, stepNumber, explanation));
        }

        return steps;
    }

    private static class NodeDistance {
        int nodeId;
        double distance;

        NodeDistance(int nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }

        double getDistance() {
            return distance;
        }
    }

    public static int NO_ID_PRESENT = -2;
}