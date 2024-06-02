package com.example.graphlet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.graphlet.db.Graph;
import com.example.graphlet.db.GraphDatabaseHelper;

public class GraphManagementActivity extends AppCompatActivity implements GraphListFragment.OnGraphSelectedListener {

    private Button buttonAll;
    private Button buttonBack;
    private Button buttonCreate;
    private Button buttonModify;
    private Button buttonRemove;
    private GraphListFragment graphListFragment;
    private Graph selectedGraph;
    private GraphDatabaseHelper graphDatabaseHelper;

    private final ActivityResultLauncher<Intent> createGraphLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Refresh the GraphListFragment
                    if (graphListFragment != null) {
                        graphListFragment.refreshData();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_management);

        graphDatabaseHelper = new GraphDatabaseHelper(this);

        buttonAll = findViewById(R.id.button_all);
        buttonBack = findViewById(R.id.button_back);
        buttonCreate = findViewById(R.id.button_create);
        buttonModify = findViewById(R.id.button_modify);
        buttonRemove = findViewById(R.id.button_remove);

        // Load the GraphListFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        graphListFragment = new GraphListFragment();
        fragmentTransaction.add(R.id.fragment_container, graphListFragment);
        fragmentTransaction.commit();

        graphListFragment.setOnGraphSelectedListener(this);

        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "All" button click
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this activity
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GraphManagementActivity.this, GraphCreationActivity.class);
                createGraphLauncher.launch(intent);
            }
        });

        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedGraph != null) {
                    Intent intent = new Intent(GraphManagementActivity.this, GraphCreationActivity.class);
                    intent.putExtra(GraphCreationActivity.EXTRA_MODE, GraphCreationActivity.Mode.MODIFY);
                    intent.putExtra(GraphCreationActivity.EXTRA_GRAPH, selectedGraph);
                    createGraphLauncher.launch(intent);
                }
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedGraph != null) {
                    graphDatabaseHelper.deleteGraph(selectedGraph.id);
                    selectedGraph = null;
                    graphListFragment.refreshData();
                    Toast.makeText(GraphManagementActivity.this, "Graph removed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GraphManagementActivity.this, "Please select a graph to remove", Toast.LENGTH_SHORT).show();
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
        System.out.println(graph.type.toString() + " STEFAN IN onGraphViewRequested");
        Intent intent = new Intent(GraphManagementActivity.this, GraphCreationActivity.class);
        intent.putExtra(GraphCreationActivity.EXTRA_MODE, GraphCreationActivity.Mode.VIEW);
        intent.putExtra(GraphCreationActivity.EXTRA_GRAPH, graph);
        createGraphLauncher.launch(intent);
    }
}