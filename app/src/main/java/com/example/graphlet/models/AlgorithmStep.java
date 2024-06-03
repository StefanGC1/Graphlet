package com.example.graphlet.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmStep implements Serializable {
    public int currentNodeId;
    public Map<Integer, Double> distances;
    public Map<Integer, Integer> previousNodes;
    public int stepNumber;
    public String explanation;

    public AlgorithmStep(int currentNodeId, Map<Integer, Double> distances, Map<Integer, Integer> previousNodes, int stepNumber, String explanation) {
        this.currentNodeId = currentNodeId;
        this.distances = new HashMap<>(distances);
        this.previousNodes = new HashMap<>(previousNodes);
        this.stepNumber = stepNumber;
        this.explanation = explanation;
    }
}
