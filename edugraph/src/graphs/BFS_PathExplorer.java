package graphs;

import java.util.*;

public class BFS_PathExplorer {

    private final CourseGraph graph;

    public BFS_PathExplorer(CourseGraph graph) {
        this.graph = graph;
    }

    // BFS traversal - visits level by level using a queue
    public List<String> bfsTraversal(String startId) {
        List<String> visited = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> seen = new HashSet<>();

        queue.add(startId);
        seen.add(startId);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            visited.add(current);

            for (CourseGraph.Edge edge : graph.getNeighbours(current)) {
                if (!seen.contains(edge.to)) {
                    seen.add(edge.to);
                    queue.add(edge.to);
                }
            }
        }
        return visited;
    }

    // BFS finds shortest path (fewest hops) in an unweighted graph
    public List<String> shortestHopPath(String startId, String targetId) {
        Map<String, String> parent = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> seen = new HashSet<>();

        queue.add(startId);
        seen.add(startId);
        parent.put(startId, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(targetId)) break;

            for (CourseGraph.Edge edge : graph.getNeighbours(current)) {
                if (!seen.contains(edge.to)) {
                    seen.add(edge.to);
                    parent.put(edge.to, current);
                    queue.add(edge.to);
                }
            }
        }

        if (!parent.containsKey(targetId)) return Collections.emptyList();

        LinkedList<String> path = new LinkedList<>();
        for (String cur = targetId; cur != null; cur = parent.get(cur))
            path.addFirst(cur);
        return path;
    }
}
