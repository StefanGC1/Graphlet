package com.example.graphlet.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public static int NO_ID_PRESENT = -2;
}