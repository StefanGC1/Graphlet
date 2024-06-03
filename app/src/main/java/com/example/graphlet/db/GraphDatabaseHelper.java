package com.example.graphlet.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.graphlet.models.Graph;

import java.util.ArrayList;
import java.util.List;

public class GraphDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "graphs.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_GRAPHS = "graphs";
    private static final String COLUMN_GRAPH_ID = "id";
    private static final String COLUMN_GRAPH_NAME = "name";
    private static final String COLUMN_GRAPH_TYPE = "type";
    private static final String COLUMN_GRAPH_NODES = "nodes";
    private static final String COLUMN_GRAPH_EDGES = "edges";

    public GraphDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableGraphs = "CREATE TABLE " + TABLE_GRAPHS + " (" +
                COLUMN_GRAPH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GRAPH_NAME + " TEXT, " +
                COLUMN_GRAPH_TYPE + " INTEGER, " +
                COLUMN_GRAPH_NODES + " TEXT, " +
                COLUMN_GRAPH_EDGES + " TEXT)";
        db.execSQL(createTableGraphs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRAPHS);
        onCreate(db);
    }

    public long addGraph(Graph graph) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRAPH_NAME, graph.name);
        values.put(COLUMN_GRAPH_TYPE, graph.type.ordinal());
        values.put(COLUMN_GRAPH_NODES, nodesToString(graph.nodes));
        values.put(COLUMN_GRAPH_EDGES, edgesToString(graph.edges));

        long id = db.insert(TABLE_GRAPHS, null, values);
        db.close();
        return id;
    }

    public Graph getGraph(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRAPHS, new String[]{COLUMN_GRAPH_ID, COLUMN_GRAPH_NAME, COLUMN_GRAPH_TYPE, COLUMN_GRAPH_NODES, COLUMN_GRAPH_EDGES},
                COLUMN_GRAPH_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Graph graph = new Graph(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_TYPE))
        );
        graph.nodes = stringToNodes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_NODES)));
        graph.edges = stringToEdges(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_EDGES)));

        cursor.close();
        return graph;
    }

    public List<Graph> getAllGraphs() {
        List<Graph> graphList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_GRAPHS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Graph graph = new Graph(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_TYPE))
                );
                graph.nodes = stringToNodes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_NODES)));
                graph.edges = stringToEdges(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_EDGES)));

                graphList.add(graph);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return graphList;
    }

    public int updateGraph(long id, Graph graph) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRAPH_NAME, graph.name);
        values.put(COLUMN_GRAPH_TYPE, graph.type.ordinal());
        values.put(COLUMN_GRAPH_NODES, nodesToString(graph.nodes));
        values.put(COLUMN_GRAPH_EDGES, edgesToString(graph.edges));

        return db.update(TABLE_GRAPHS, values, COLUMN_GRAPH_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteGraph(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GRAPHS, COLUMN_GRAPH_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    private String nodesToString(List<Graph.Node> nodes) {
        StringBuilder sb = new StringBuilder();
        for (Graph.Node node : nodes) {
            sb.append(node.id).append(",").append(node.x).append(",").append(node.y).append(";");
        }
        return sb.toString();
    }

    private List<Graph.Node> stringToNodes(String str) {
        List<Graph.Node> nodes = new ArrayList<>();
        String[] nodeStrings = str.split(";");
        for (String nodeString : nodeStrings) {
            if (!nodeString.isEmpty()) {
                String[] nodeAttributes = nodeString.split(",");
                nodes.add(new Graph.Node(Integer.parseInt(nodeAttributes[0]), Float.parseFloat(nodeAttributes[1]), Float.parseFloat(nodeAttributes[2])));
            }
        }
        return nodes;
    }

    private String edgesToString(List<Graph.Edge> edges) {
        StringBuilder sb = new StringBuilder();
        for (Graph.Edge edge : edges) {
            sb.append(edge.nodeId1).append(",").append(edge.nodeId2).append(",").append(edge.weight).append(",").append(edge.isDirected ? 1 : 0).append(";");
        }
        return sb.toString();
    }

    private List<Graph.Edge> stringToEdges(String str) {
        List<Graph.Edge> edges = new ArrayList<>();
        String[] edgeStrings = str.split(";");
        for (String edgeString : edgeStrings) {
            if (!edgeString.isEmpty()) {
                String[] edgeAttributes = edgeString.split(",");
                edges.add(
                    new Graph.Edge(
                        Integer.parseInt(edgeAttributes[0]),
                        Integer.parseInt(edgeAttributes[1]),
                        Double.parseDouble(edgeAttributes[2]),
                        Integer.parseInt(edgeAttributes[3]) == 1));
            }
        }
        return edges;
    }
}