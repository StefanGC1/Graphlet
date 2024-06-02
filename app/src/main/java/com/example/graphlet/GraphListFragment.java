package com.example.graphlet;

import android.graphics.Color;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.graphlet.db.Graph;
import com.example.graphlet.db.GraphDatabaseHelper;

import java.util.List;

public class GraphListFragment extends Fragment {
    private ListView listView;
    private GraphDatabaseHelper graphDatabaseHelper;
    private List<Graph> graphList;
    private GraphListAdapter adapter;
    private Graph selectedGraph;
    private OnGraphSelectedListener listener;
    private long lastTapTime = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final long DOUBLE_TAP_THRESHOLD = 300;

    public interface OnGraphSelectedListener {
        void onGraphSelected(Graph graph);
        void onGraphViewRequested(Graph graph);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_list, container, false);

        listView = view.findViewById(R.id.list_graphs);
        graphDatabaseHelper = new GraphDatabaseHelper(getActivity());

        // Load data from database
        loadGraphData();

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Graph graph = graphList.get(position);
            long currentTime = System.currentTimeMillis();

            if (selectedGraph != null && selectedGraph.equals(graph)) {
                System.out.println(graph.type.toString() + " STEFAN IN GraphListFragment");
                if (currentTime - lastTapTime < DOUBLE_TAP_THRESHOLD) {
                    if (listener != null) {
                        listener.onGraphViewRequested(graph);
                    }
                } else {
                    selectedGraph = null;
                    adapter.notifyDataSetChanged();
                }
            } else {
                selectedGraph = graph;
                adapter.notifyDataSetChanged();
                if (listener != null) {
                    listener.onGraphSelected(graph);
                }
            }

            lastTapTime = currentTime;
        });

        return view;
    }

    public void setOnGraphSelectedListener(OnGraphSelectedListener listener) {
        this.listener = listener;
    }

    private void loadGraphData() {
        graphList = graphDatabaseHelper.getAllGraphs();
        adapter = new GraphListAdapter(getActivity(), graphList);
        listView.setAdapter(adapter);
    }

    public void refreshData() {
        loadGraphData();
        adapter.notifyDataSetChanged();
    }

    private class GraphListAdapter extends ArrayAdapter<Graph> {
        public GraphListAdapter(@NonNull Context context, @NonNull List<Graph> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            Graph graph = getItem(position);
            TextView textView = convertView.findViewById(android.R.id.text1);
            assert graph != null;
            textView.setText(graph.name);

            if (graph.equals(selectedGraph)) {
                textView.setTextColor(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
            } else {
                textView.setTextColor(Color.BLACK); // Default color
            }

            return convertView;
        }
    }

    public Graph getSelectedGraph() {
        return selectedGraph;
    }
}
