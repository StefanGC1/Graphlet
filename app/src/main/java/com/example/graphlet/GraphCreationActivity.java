package com.example.graphlet;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graphlet.models.Graph;
import com.example.graphlet.db.GraphDatabaseHelper;

import java.util.Objects;

public class GraphCreationActivity extends AppCompatActivity implements GridPlaneView.OnEdgeDeletedListener {
    public static final String EXTRA_MODE = "com.example.graphlet.MODE";
    public static final String EXTRA_GRAPH = "com.example.graphlet.GRAPH";

    public enum Mode {
        CREATE,
        MODIFY,
        VIEW
    }


    private Button buttonAll;
    private Button buttonBack;
    private Button buttonAdd;
    private ImageView buttonUpArrow;
    private ImageView buttonDownArrow;
    private Button buttonRemove;
    private Button buttonLink;
    private Button buttonCreateGraph;
    private GridPlaneView gridPlaneView;
    private ScrollView slidingWindow;
    private EditText editTextGraphName;
    private Spinner spinnerGraphType;
    private LinearLayout edgeListContainer;
    private boolean isAddingMode = false;
    private boolean isWindowVisible = false;

    private Mode mode;
    private GraphDatabaseHelper graphDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_creation);

        graphDatabaseHelper = new GraphDatabaseHelper(this);

        buttonAll = findViewById(R.id.button_all);
        buttonBack = findViewById(R.id.button_back);
        buttonAdd = findViewById(R.id.button_add);
        buttonUpArrow = findViewById(R.id.button_up_arrow);
        buttonDownArrow = findViewById(R.id.button_down_arrow);
        buttonRemove = findViewById(R.id.button_remove);
        buttonLink = findViewById(R.id.button_link);
        buttonCreateGraph = findViewById(R.id.button_create_graph);
        gridPlaneView = findViewById(R.id.grid_plane);
        slidingWindow = findViewById(R.id.sliding_window);
        editTextGraphName = findViewById(R.id.edit_text_graph_name);
        spinnerGraphType = findViewById(R.id.spinner_graph_type);
        edgeListContainer = findViewById(R.id.edge_list_container);

        slidingWindow.setVisibility(View.INVISIBLE);
        slidingWindow.setTranslationY(500);

        gridPlaneView.setOnEdgeDeletedListener(this);

        ArrayAdapter<Graph.GraphType> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Graph.GraphType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGraphType.setAdapter(adapter);

        mode = (Mode) getIntent().getSerializableExtra(EXTRA_MODE);
        mode = mode != null ? mode : Mode.CREATE;
        handleMode();

        spinnerGraphType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Graph.GraphType selectedType = (Graph.GraphType) parent.getItemAtPosition(position);
                gridPlaneView.setGraphType(selectedType);
                updateEdgeList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAddingMode = !isAddingMode;
                gridPlaneView.setAddingMode(isAddingMode);
                buttonAdd.setText(isAddingMode ? "Stop" : "Add");
            }
        });

        buttonUpArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWindowVisible) {
                    slideWindowUp();
                }
            }
        });

        buttonDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWindowVisible) {
                    slideWindowDown();
                }
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridPlaneView.removeSelectedNodes();
                gridPlaneView.removeSelectedEdges();
            }
        });

        buttonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridPlaneView.linkSelectedNodes();
                updateEdgeList();
            }
        });

        buttonCreateGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String graphName = editTextGraphName.getText().toString().trim();
                Graph.GraphType graphType = (Graph.GraphType) spinnerGraphType.getSelectedItem();
                if (!graphName.isEmpty()) {
                    Graph graph = gridPlaneView.getGraph(graphName);
                    long id = 0;

                    if (mode == Mode.CREATE) {
                        id = graphDatabaseHelper.addGraph(graph);
                    } else {
                        long graphId = ((Graph) Objects.requireNonNull(getIntent().getSerializableExtra(EXTRA_GRAPH))).id;
                        id = graphDatabaseHelper.updateGraph(graphId, graph);
                    }

                    if (id > 0) {
                        Toast.makeText(GraphCreationActivity.this, "Graph created successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(GraphCreationActivity.this, "Error creating graph", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GraphCreationActivity.this, "Please enter a graph name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateEdgeList();
    }

    private void handleMode() {
        if (mode == Mode.CREATE) {
        } else if (mode == Mode.MODIFY) {
            Graph graph = (Graph) getIntent().getSerializableExtra(EXTRA_GRAPH);
            if (graph != null) {
                loadGraph(graph);
            }
        } else if (mode == Mode.VIEW) {
            Graph graph = (Graph) getIntent().getSerializableExtra(EXTRA_GRAPH);
            if (graph != null) {
                loadGraph(graph);
            }

            buttonAdd.setVisibility(View.INVISIBLE);
            buttonUpArrow.setVisibility(View.INVISIBLE);
            buttonDownArrow.setVisibility(View.INVISIBLE);
            buttonRemove.setVisibility(View.INVISIBLE);
            buttonCreateGraph.setVisibility(View.INVISIBLE);
            buttonLink.setVisibility(View.INVISIBLE);

            gridPlaneView.setAddingMode(false);
            gridPlaneView.setTouchEventEnabled(false);
        }
    }

    private void loadGraph(Graph graph) {
        System.out.println(graph.type.toString() + " STEFAN IN loadGraph");
        editTextGraphName.setText(graph.name);
        spinnerGraphType.setSelection(graph.type.ordinal());
        gridPlaneView.setNodes(graph.nodes);
        gridPlaneView.setEdges(graph.edges);
        gridPlaneView.setGraphType(graph.type);
        gridPlaneView.invalidate();
        updateEdgeList();
    }

    @Override
    public void updateEdgeList() {
        edgeListContainer.removeAllViews();
        for (Graph.Edge edge : gridPlaneView.getEdges()) {
            View edgeView = getLayoutInflater().inflate(R.layout.item_edge, edgeListContainer, false);

            TextView edgeText = edgeView.findViewById(R.id.edge_text);
            EditText edgeWeight = edgeView.findViewById(R.id.edge_weight);

            Graph.Node node1 = gridPlaneView.getNodeById(edge.nodeId1);
            Graph.Node node2 = gridPlaneView.getNodeById(edge.nodeId2);
            if (node1 != null && node2 != null) {
                edgeText.setText("Edge" + node1.id + (edge.isDirected ? " -> " : " - ") + node2.id);
                edgeWeight.setText(String.valueOf(edge.weight));
                edgeWeight.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus) {
                        try {
                            double weight = Double.parseDouble(edgeWeight.getText().toString());
                            edge.weight = weight;
                            gridPlaneView.updateEdgeWeight(edge.nodeId1, edge.nodeId2, weight);
                        } catch (NumberFormatException e) {
                            edgeWeight.setText(String.valueOf(edge.weight));
                        }
                    }
                });
                edgeListContainer.addView(edgeView);
            }
        }
    }

    private void slideWindowUp() {
        System.out.println("STEFAN in modify spinner type: " + (Graph.GraphType) spinnerGraphType.getSelectedItem());
        isWindowVisible = true;
        slidingWindow.setVisibility(View.VISIBLE);
        slidingWindow.bringToFront();
        buttonUpArrow.setAlpha(0f);
        buttonDownArrow.setAlpha(1f);

        ObjectAnimator.ofFloat(slidingWindow, "translationY", slidingWindow.getHeight(), 0).setDuration(300).start();
    }

    private void slideWindowDown() {
        isWindowVisible = false;
        buttonUpArrow.setVisibility(View.VISIBLE);
        buttonUpArrow.setAlpha(1f);
        buttonDownArrow.setAlpha(0f);

        ObjectAnimator.ofFloat(slidingWindow, "translationY", 0, slidingWindow.getHeight()).setDuration(300).start();
        slidingWindow.postDelayed(new Runnable() {
            @Override
            public void run() {
                slidingWindow.setVisibility(View.GONE);
            }
        }, 300);
    }
}