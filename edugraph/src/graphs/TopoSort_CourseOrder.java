package graphs;

import java.util.*;

// Kahn's algorithm (BFS-based) topological sort
// Returns a valid course completion order for a DAG
public class TopoSort_CourseOrder {

    private final CourseGraph graph;

    public TopoSort_CourseOrder(CourseGraph graph) {
        this.graph = graph;
    }

    public List<String> sort() {
        // Step 1: Compute in-degree for every vertex
        Map<String, Integer> inDegree = new HashMap<>();
        for (String id : graph.getAllCourseIds()) inDegree.put(id, 0);
        for (String id : graph.getAllCourseIds())
            for (CourseGraph.Edge edge : graph.getNeighbours(id))
                inDegree.merge(edge.to, 1, Integer::sum);

        // Step 2: Add nodes with no prerequisites to the queue
        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> e : inDegree.entrySet())
            if (e.getValue() == 0) queue.add(e.getKey());

        List<String> order = new ArrayList<>();

        // Step 3: Process queue - reduce in-degree of neighbours
        while (!queue.isEmpty()) {
            String current = queue.poll();
            order.add(current);
            for (CourseGraph.Edge edge : graph.getNeighbours(current)) {
                inDegree.merge(edge.to, -1, Integer::sum);
                if (inDegree.get(edge.to) == 0)
                    queue.add(edge.to);
            }
        }

        if (order.size() < graph.vertexCount())
            System.out.println("  [TopoSort] WARNING: Cycle detected - invalid prerequisites!");

        return order;
    }
}
