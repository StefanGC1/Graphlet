package com.example.graphlet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.graphlet.models.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GridPlaneView extends View {

    private OnEdgeDeletedListener listener;
    private Paint paint;
    private Paint nodePaint;
    private Paint selectedNodePaint;
    private Paint nodeFillPaint;
    private Paint selectedNodeFillPaint;
    private Paint edgePaint;
    private Paint selectedEdgePaint;
    private Paint textPaint;
    private Paint edgeTextPaint;
    private boolean isAddingMode;
    private List<Graph.Node> nodes;
    private List<Graph.Edge> edges;
    private List<Graph.Node> selectedNodes;
    private Graph.GraphType graphType;
    private boolean isTouchEventEnabled = true;

    public GridPlaneView(Context context) {
        super(context);
        init();
    }

    public GridPlaneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridPlaneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFFCCCCCC); // light gray color
        paint.setStrokeWidth(2);

        int primaryColor = ContextCompat.getColor(this.getContext(), R.color.colorPrimary);

        nodePaint = new Paint();
        nodePaint.setColor(Color.BLACK);
        nodePaint.setStyle(Paint.Style.STROKE);
        nodePaint.setStrokeWidth(3);

        selectedNodePaint = new Paint();
        selectedNodePaint.setColor(primaryColor);
        selectedNodePaint.setStyle(Paint.Style.STROKE);
        selectedNodePaint.setStrokeWidth(3);

        nodeFillPaint = new Paint();
        nodeFillPaint.setColor(Color.WHITE);
        nodeFillPaint.setStyle(Paint.Style.FILL);

        selectedNodeFillPaint = new Paint();
        selectedNodeFillPaint.setColor(primaryColor);
        selectedNodeFillPaint.setStyle(Paint.Style.FILL);

        edgePaint = new Paint();
        edgePaint.setColor(Color.BLACK);
        edgePaint.setStrokeWidth(3);

        selectedEdgePaint = new Paint();
        selectedEdgePaint.setColor(primaryColor);
        selectedEdgePaint.setStrokeWidth(3);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(30);

        edgeTextPaint = new Paint();
        edgeTextPaint.setColor(Color.BLACK);
        edgeTextPaint.setTextAlign(Paint.Align.CENTER);
        edgeTextPaint.setTextSize(30);

        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        selectedNodes = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int stepSize = 50; // size of each grid square

        // Draw vertical lines
        for (int i = 0; i <= width; i += stepSize) {
            canvas.drawLine(i, 0, i, height, paint);
        }

        // Draw horizontal lines
        for (int i = 0; i <= height; i += stepSize) {
            canvas.drawLine(0, i, width, i, paint);
        }

        for (Graph.Edge edge : edges) {
            Graph.Node node1 = getNodeById(edge.nodeId1);
            Graph.Node node2 = getNodeById(edge.nodeId2);
            if (node1 != null && node2 != null) {
                Paint p = edge.isSelected ? selectedEdgePaint : edgePaint;
                float[] adjustedPoints = getAdjustedEdgePoints(node1.x, node1.y, node2.x, node2.y, 40);
                canvas.drawLine(adjustedPoints[0], adjustedPoints[1], adjustedPoints[2], adjustedPoints[3], p);

                if (edge.isDirected) {
                    drawArrow(canvas, adjustedPoints[0], adjustedPoints[1], adjustedPoints[2], adjustedPoints[3], edge.isSelected);
                }

                float midX = (adjustedPoints[0] + adjustedPoints[2]) / 2;
                float midY = (adjustedPoints[1] + adjustedPoints[3]) / 2;

                // Calculate the perpendicular offset
                float dx = adjustedPoints[2] - adjustedPoints[0];
                float dy = adjustedPoints[3] - adjustedPoints[1];
                float length = (float) Math.sqrt(dx * dx + dy * dy);
                float offsetX = -dy / length * 30; // 20 pixels offset
                float offsetY = dx / length * 30;

                canvas.drawText(String.valueOf(edge.weight), midX - offsetX, midY - offsetY - ((edgeTextPaint.descent() + edgeTextPaint.ascent()) / 2), edgeTextPaint);
            }
        }

        // Draw nodes
        for (Graph.Node node : nodes) {
            Paint p = node.isSelected ? selectedNodePaint : nodePaint;
            Paint fillPaint = node.isSelected ? selectedNodeFillPaint : nodeFillPaint;
            canvas.drawCircle(node.x, node.y, 40, fillPaint);
            canvas.drawCircle(node.x, node.y, 40, p);
            // Draw node ID at the center
            canvas.drawText(String.valueOf(node.id), node.x, node.y - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
        }
    }

    private void drawArrow(Canvas canvas, float x1, float y1, float x2, float y2, boolean isSelected) {
        float arrowHeadLength = 40;
        float arrowHeadAngle = (float) Math.toRadians(45);

        float dx = x2 - x1;
        float dy = y2 - y1;
        float angle = (float) Math.atan2(dy, dx);

        float xArrow1 = x2 - arrowHeadLength * (float) Math.cos(angle - arrowHeadAngle);
        float yArrow1 = y2 - arrowHeadLength * (float) Math.sin(angle - arrowHeadAngle);
        float xArrow2 = x2 - arrowHeadLength * (float) Math.cos(angle + arrowHeadAngle);
        float yArrow2 = y2 - arrowHeadLength * (float) Math.sin(angle + arrowHeadAngle);

        Path path = new Path();
        path.moveTo(x2, y2);
        path.lineTo(xArrow1, yArrow1);
        path.lineTo(xArrow2, yArrow2);
        path.close();

        canvas.drawPath(path, isSelected ? selectedEdgePaint : edgePaint);
    }

    private float[] getAdjustedEdgePoints(float x1, float y1, float x2, float y2, float radius) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float ratio = radius / distance;

        float x1Adjusted = x1 + dx * ratio;
        float y1Adjusted = y1 + dy * ratio;
        float x2Adjusted = x2 - dx * ratio;
        float y2Adjusted = y2 - dy * ratio;

        return new float[]{x1Adjusted, y1Adjusted, x2Adjusted, y2Adjusted};
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouchEventEnabled) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            if (isAddingMode) {
                int nodeId = nodes.size() + 1; // simple way to assign IDs
                nodes.add(new Graph.Node(nodeId, x, y));
            } else {
                for (Graph.Node node : nodes) {
                    if (isWithinNode(x, y, node)) {
                        node.isSelected = !node.isSelected;
                        if (node.isSelected) {
                            selectedNodes.add(node);
                        } else {
                            selectedNodes.remove(node);
                        }
                    }
                }
                for (Graph.Edge edge : edges) {
                    if (isWithinEdge(x, y, edge)) {
                        edge.isSelected = !edge.isSelected;
                    }
                }
            }
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean isWithinNode(float x, float y, Graph.Node node) {
        float dx = x - node.x;
        float dy = y - node.y;
        return dx * dx + dy * dy <= 1600;
    }

    private boolean isWithinEdge(float x, float y, Graph.Edge edge) {
        Graph.Node node1 = getNodeById(edge.nodeId1);
        Graph.Node node2 = getNodeById(edge.nodeId2);
        if (node1 == null || node2 == null) return false;

        if (isWithinNode(x, y, node1) || isWithinNode(x, y, node2)) {
            return false;
        }

        float dx = node2.x - node1.x;
        float dy = node2.y - node1.y;
        float lengthSquared = dx * dx + dy * dy;
        float projection = ((x - node1.x) * dx + (y - node1.y) * dy) / lengthSquared;
        if (projection < 0 || projection > 1) return false;

        float closestX = node1.x + projection * dx;
        float closestY = node1.y + projection * dy;
        float distanceSquared = (x - closestX) * (x - closestX) + (y - closestY) * (y - closestY);
        return distanceSquared <= 400; // 20^2, assuming a tolerance radius of 20
    }

    public void setAddingMode(boolean isAddingMode) {
        this.isAddingMode = isAddingMode;
    }

    public void removeSelectedNodes() {
        List<Graph.Node> toRemove = new ArrayList<>();
        List<Integer> removedNodeIds = new ArrayList<>();
        int highestIdRemoved = -1;
        for (Graph.Node node : nodes) {
            if (node.isSelected) {
                toRemove.add(node);
                removedNodeIds.add(node.id);
                highestIdRemoved = Math.max(node.id, highestIdRemoved);
            }
        }
        nodes.removeAll(toRemove);

        if (highestIdRemoved == -1)
            return;

        int idsRemoved = removedNodeIds.size();
        for (Graph.Node node : nodes) {
            if (node.id > highestIdRemoved)
                node.id = node.id - idsRemoved;
        }

        // Remove edges connected to the removed nodes
        List<Graph.Edge> edgesToRemove = new ArrayList<>();
        for (Graph.Edge edge : edges) {
            if (removedNodeIds.contains(edge.nodeId1) || removedNodeIds.contains(edge.nodeId2)) {
                edgesToRemove.add(edge);
            }
        }
        edges.removeAll(edgesToRemove);

        invalidate();
    }

    public void removeSelectedEdges() {
        List<Graph.Edge> toRemove = new ArrayList<>();
        for (Graph.Edge edge : edges) {
            if (edge.isSelected) {
                toRemove.add(edge);
            }
        }
        edges.removeAll(toRemove);
        listener.updateEdgeList();
        invalidate();
    }

    public void linkSelectedNodes() {
        if (selectedNodes.size() >= 2) {
            Graph.Node node1 = selectedNodes.get(0);
            Graph.Node node2 = selectedNodes.get(1);
            edges.add(new Graph.Edge(node1.id, node2.id, graphType == Graph.GraphType.DIJKSTRA_DIRECTED));
            node1.isSelected = false;
            node2.isSelected = false;
            selectedNodes.clear();
            invalidate();
        }
    }

    Graph.Node getNodeById(int id) {
        for (Graph.Node node : nodes) {
            if (node.id == id) {
                return node;
            }
        }
        return null;
    }

    public Graph getGraph(String name) {
        Graph graph = new Graph(0, name, graphType);

        for (Graph.Edge edge : edges) {
            System.out.println("STEFAN IN getGraph, edge is: " + edge.isDirected);
        }

        graph.nodes.addAll(nodes);
        graph.edges.addAll(edges);
        return graph;
    }

    public void setGraphType(Graph.GraphType graphType) {
        this.graphType = graphType;
        for (Graph.Edge edge : edges) {
            edge.isDirected = (graphType == Graph.GraphType.DIJKSTRA_DIRECTED);
        }
        invalidate();
    }

    public void setNodes(List<Graph.Node> nodes) {
        this.nodes = nodes;
    }
    public void setEdges(List<Graph.Edge> edges) {
        this.edges = edges;
    }

    public List<Graph.Node> getNodes() {
        return nodes;
    }
    public List<Graph.Edge> getEdges() {
        return edges;
    }

    public void setTouchEventEnabled(boolean enabled) {
        isTouchEventEnabled = enabled;
    }

    public void updateEdgeWeight(int nodeId1, int nodeId2, double weight) {
        for (Graph.Edge edge : edges) {
            if (edge.nodeId1 == nodeId1 && edge.nodeId2 == nodeId2) {
                edge.weight = weight;
                invalidate();
                return;
            }
        }
    }

    public void setSelectedNodes(List<Integer> nodeIds) {
        for (Graph.Node node : nodes) {
            node.isSelected = nodeIds.contains(node.id);
        }
        invalidate();
    }

    public void setSelectedEdges(List<Integer[]> edgeIds) {
        for (Graph.Edge edge : edges) {
            edge.isSelected = false;
            for (Integer[] edgeId : edgeIds) {
                if ((edge.nodeId1 == edgeId[0] && edge.nodeId2 == edgeId[1]) ||
                        (edge.nodeId1 == edgeId[1] && edge.nodeId2 == edgeId[0])) {
                    edge.isSelected = true;
                    break;
                }
            }
        }
        invalidate();
    }

    public void setSelectedEdges(Map<Integer, Integer> previousNodes, Integer endNodeId) {
        List<Integer[]> selectedEdges = new ArrayList<>();

        List<Integer[]> edgeIds = new ArrayList<>();
        for (int nodeId : previousNodes.keySet()) {
            if (previousNodes.get(nodeId) != -1) {
                selectedEdges.add(new Integer[]{nodeId, previousNodes.get(nodeId)});
            }
        }

        for (Graph.Edge edge : edges) {
            edge.isSelected = false;
            for (Integer[] edgeId : edgeIds) {
                if ((edge.nodeId1 == edgeId[0] && edge.nodeId2 == edgeId[1]) ||
                        (edge.nodeId1 == edgeId[1] && edge.nodeId2 == edgeId[0])) {
                    edge.isSelected = true;
                    break;
                }
            }
        }
        invalidate();
    }

    public void setEdgeWeight(int nodeId1, int nodeId2, double weight) {
        for (Graph.Edge edge : edges) {
            if (edge.nodeId1 == nodeId1 && edge.nodeId2 == nodeId2) {
                edge.weight = weight;
                invalidate();
                return;
            }
        }
    }

    public interface OnEdgeDeletedListener {
        public void updateEdgeList();
    }

    public void setOnEdgeDeletedListener(OnEdgeDeletedListener listener) {
        this.listener = listener;
    }
}
