package graphs;

import java.util.*;

// Dijkstra: shortest path by edge weight (difficulty)
public class Dijkstra_LearningPath {

    private final CourseGraph graph;
    private Map<String, String> parent;

    public Dijkstra_LearningPath(CourseGraph graph) {
        this.graph = graph;
        this.parent = new HashMap<>();
    }

    public Map<String, Integer> computeDistances(String sourceId) {
        Map<String, Integer> dist = new HashMap<>();
        parent = new HashMap<>();

        for (String id : graph.getAllCourseIds()) dist.put(id, Integer.MAX_VALUE);
        dist.put(sourceId, 0);

        // Min-heap: [distance, courseId]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        List<String> idList = new ArrayList<>(graph.getAllCourseIds());
        Map<String, Integer> idxMap = new HashMap<>();
        for (int i = 0; i < idList.size(); i++) idxMap.put(idList.get(i), i);

        pq.offer(new int[]{0, idxMap.getOrDefault(sourceId, 0)});
        parent.put(sourceId, null);

        Set<String> settled = new HashSet<>();

        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int cost = top[0];
            String current = (top[1] < idList.size()) ? idList.get(top[1]) : null;

            if (current == null || settled.contains(current)) continue;
            settled.add(current);

            for (CourseGraph.Edge edge : graph.getNeighbours(current)) {
                int newDist = cost + edge.weight;
                if (newDist < dist.getOrDefault(edge.to, Integer.MAX_VALUE)) {
                    dist.put(edge.to, newDist);
                    parent.put(edge.to, current);
                    int idx = idxMap.getOrDefault(edge.to, -1);
                    if (idx >= 0) pq.offer(new int[]{newDist, idx});
                }
            }
        }
        return dist;
    }

    public List<String> getShortestPath(String sourceId, String targetId) {
        computeDistances(sourceId);

        if (!parent.containsKey(targetId)) {
            System.out.println("  [Dijkstra] No path from " + sourceId + " to " + targetId);
            return Collections.emptyList();
        }

        LinkedList<String> path = new LinkedList<>();
        for (String cur = targetId; cur != null; cur = parent.get(cur))
            path.addFirst(cur);
        return path;
    }
}
