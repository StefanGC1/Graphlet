package com.example.graphlet;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graphlet.models.AlgorithmStep;
import com.example.graphlet.models.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphSolverActivity extends AppCompatActivity {

    public static final String EXTRA_GRAPH = "com.example.graphlet.GRAPH";

    private Graph graph;
    private GridPlaneView gridPlaneView;
    private LinearLayout bottomLayout;
    private TextView infoText;
    private Spinner startNodeSpinner;
    private Spinner endNodeSpinner;
    private Button continueButton;
    private ScrollView scrollView;
    private TextView algorithmStepText;
    private Button nextButton;

    private int currentStep = 0;
    private int startNodeId = -1;
    private int endNodeId = -1;
    private List<AlgorithmStep> algorithmSteps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_solver);

        graph = (Graph) getIntent().getSerializableExtra(EXTRA_GRAPH);

        gridPlaneView = findViewById(R.id.grid_plane_view);
        bottomLayout = findViewById(R.id.bottom_layout);
        infoText = findViewById(R.id.info_text);
        startNodeSpinner = findViewById(R.id.start_node_spinner);
        endNodeSpinner = findViewById(R.id.end_node_spinner);
        continueButton = findViewById(R.id.continue_button);
        scrollView = findViewById(R.id.scroll_view);
        algorithmStepText = findViewById(R.id.algorithm_step_text);
        nextButton = findViewById(R.id.next_button);

        // Set graph properties in GridPlaneView
        gridPlaneView.setNodes(graph.nodes);
        gridPlaneView.setEdges(graph.edges);
        gridPlaneView.setGraphType(graph.type);

        // Populate spinners with node IDs
        populateNodeSpinners();

        // Set listeners for the "Continue" button
        continueButton.setOnClickListener(v -> {
            startNodeId = (int) startNodeSpinner.getSelectedItem();
            endNodeId = (int) endNodeSpinner.getSelectedItem();
            startAlgorithmVisualizerPhase();
        });

        // Set listener for the "Next" button
        nextButton.setOnClickListener(v -> {
            if (nextButton.getText().toString().equals("Finish"))
                finish();
            executeAlgorithmStep();
        });

        // Start the info-fetch phase
        startInfoFetchPhase();
    }

    private void populateNodeSpinners() {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (Graph.Node node : graph.nodes) {
            adapter.add(node.id);
        }

        startNodeSpinner.setAdapter(adapter);
        endNodeSpinner.setAdapter(adapter);
    }

    private void startInfoFetchPhase() {
        // Set up the UI for info-fetch phase
        infoText.setVisibility(View.VISIBLE);
        startNodeSpinner.setVisibility(View.VISIBLE);
        endNodeSpinner.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
    }

    private void startAlgorithmVisualizerPhase() {
        // Transition to the algorithm-visualizer phase
        fadeOutBottomLayout(() -> {
            infoText.setVisibility(View.GONE);
            startNodeSpinner.setVisibility(View.GONE);
            endNodeSpinner.setVisibility(View.GONE);
            continueButton.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);

            // Initialize Dijkstra's algorithm
            initializeDijkstraAlgorithm();
        });
    }

    private void initializeDijkstraAlgorithm() {
        // Perform Dijkstra's algorithm with steps
        algorithmSteps = graph.performDijkstraWithSteps(startNodeId, endNodeId);

        // Start the first step of the algorithm
        executeAlgorithmStep();
    }

    private void executeAlgorithmStep() {
        if (currentStep < algorithmSteps.size()) {
            // Get the current step
            AlgorithmStep step = algorithmSteps.get(currentStep);

            // Highlight nodes and edges based on the current step
            List<Integer> selectedNodes = new ArrayList<>();
            selectedNodes.add(step.currentNodeId);
            gridPlaneView.setSelectedNodes(selectedNodes);

            List<Integer[]> selectedEdges = new ArrayList<>();
            for (int nodeId : step.previousNodes.keySet()) {
                if (step.previousNodes.get(nodeId) != -1) {
                    selectedEdges.add(new Integer[]{nodeId, step.previousNodes.get(nodeId)});
                }
            }
            gridPlaneView.setSelectedEdges(selectedEdges);

            // Update the algorithm step text with distances and explanations
            StringBuilder stepDescription = new StringBuilder();
            stepDescription.append(step.explanation).append("\n");
            stepDescription.append("Current distances:\n");
            for (Map.Entry<Integer, Double> entry : step.distances.entrySet()) {
                stepDescription.append("Node ").append(entry.getKey()).append(": ").append(entry.getValue() == Double.MAX_VALUE ? "∞" : entry.getValue()).append("\n");
            }

            algorithmStepText.setText(stepDescription.toString());

            // Move to the next step
            currentStep++;

            if (currentStep >= algorithmSteps.size()) {
                nextButton.setText("Finish");
            } else {
                fadeInBottomLayout();
            }
        } else {
            // Algorithm completed
            algorithmStepText.setText("Algorithm completed.");
            nextButton.setText("Finish");
        }
    }

    private void fadeOutBottomLayout(Runnable endAction) {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                endAction.run();
                fadeInBottomLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        bottomLayout.startAnimation(fadeOut);
    }

    private void fadeInBottomLayout() {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);
        bottomLayout.startAnimation(fadeIn);
    }
}
