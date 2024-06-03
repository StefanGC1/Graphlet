package com.example.graphlet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.graphlet.models.Graph;

public class AlgorithmVisualizerActivity extends AppCompatActivity implements GraphListFragment.OnGraphSelectedListener {

    private Button buttonBack;
    private Button buttonContinue;
    private GraphListFragment graphListFragment;
    private Graph selectedGraph;

    private final ActivityResultLauncher<Intent> graphViewLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Handle any result if needed
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algorithm_visualizer);

        buttonBack = findViewById(R.id.button_back);
        buttonContinue = findViewById(R.id.button_continue);

        // Load the GraphListFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        graphListFragment = new GraphListFragment();
        fragmentTransaction.add(R.id.fragment_container, graphListFragment);
        fragmentTransaction.commit();

        graphListFragment.setOnGraphSelectedListener(this);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this activity
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedGraph != null) {
                    Intent intent = new Intent(AlgorithmVisualizerActivity.this, GraphSolverActivity.class);
                    intent.putExtra(GraphSolverActivity.EXTRA_GRAPH, selectedGraph);
                    startActivity(intent);
                } else {
                    // Optionally show a message to select a graph first
                }
            }
        });
    }

    @Override
    public void onGraphSelected(Graph graph) {
        selectedGraph = graph;
    }

    @Override
    public void onGraphViewRequested(Graph graph) {
        Intent intent = new Intent(AlgorithmVisualizerActivity.this, GraphCreationActivity.class);
        intent.putExtra(GraphCreationActivity.EXTRA_MODE, GraphCreationActivity.Mode.VIEW);
        intent.putExtra(GraphCreationActivity.EXTRA_GRAPH, graph);
        graphViewLauncher.launch(intent);
    }
}
