package graphs;

import java.util.*;

public class DFS_CycleDetector {

    private final CourseGraph graph;

    public DFS_CycleDetector(CourseGraph graph) {
        this.graph = graph;
    }

    // DFS traversal from a start node
    public List<String> dfsTraversal(String startId) {
        List<String> order = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        dfsHelper(startId, visited, order);
        return order;
    }

    private void dfsHelper(String current, Set<String> visited, List<String> order) {
        visited.add(current);
        order.add(current);
        for (CourseGraph.Edge edge : graph.getNeighbours(current))
            if (!visited.contains(edge.to))
                dfsHelper(edge.to, visited, order);
    }

    // Detect cycle using visited + inStack sets
    // If we reach a node already in the current DFS path -> cycle found
    public boolean hasCycle() {
        Set<String> visited = new HashSet<>();
        Set<String> inStack = new HashSet<>();

        for (String id : graph.getAllCourseIds())
            if (!visited.contains(id))
                if (detectCycle(id, visited, inStack)) return true;
        return false;
    }

    private boolean detectCycle(String current, Set<String> visited, Set<String> inStack) {
        visited.add(current);
        inStack.add(current);

        for (CourseGraph.Edge edge : graph.getNeighbours(current)) {
            if (!visited.contains(edge.to)) {
                if (detectCycle(edge.to, visited, inStack)) return true;
            } else if (inStack.contains(edge.to)) {
                System.out.println("  [DFS] Cycle: " + current + " -> " + edge.to);
                return true;
            }
        }

        inStack.remove(current);
        return false;
    }
}
