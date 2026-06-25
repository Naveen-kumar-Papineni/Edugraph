package graphs;

import core.Course;
import java.util.*;

// Directed weighted graph: courses as vertices, prerequisites as edges
public class CourseGraph {

    public static class Edge {
        public final String to;
        public final int weight;
        public Edge(String to, int weight) { this.to = to; this.weight = weight; }
    }

    private final Map<String, List<Edge>> adjList;
    private final Map<String, Course> courses;

    public CourseGraph() {
        adjList = new LinkedHashMap<>();
        courses = new LinkedHashMap<>();
    }

    public void addCourse(Course course) {
        courses.put(course.getCourseId(), course);
        adjList.putIfAbsent(course.getCourseId(), new ArrayList<>());
    }

    // from -> to means: complete 'from' before taking 'to'
    public void addPrerequisite(String fromId, String toId) {
        Course target = courses.get(toId);
        int weight = (target != null) ? target.getDifficulty() : 1;
        adjList.computeIfAbsent(fromId, k -> new ArrayList<>()).add(new Edge(toId, weight));
    }

    public List<Edge> getNeighbours(String courseId) {
        return adjList.getOrDefault(courseId, Collections.emptyList());
    }

    public Set<String> getAllCourseIds() { return courses.keySet(); }
    public Course getCourse(String id)   { return courses.get(id); }
    public Map<String, Course> getAllCourses() { return courses; }
    public int vertexCount()             { return courses.size(); }

    public void printGraph() {
        System.out.println("  [CourseGraph - Adjacency List]");
        for (Map.Entry<String, List<Edge>> e : adjList.entrySet()) {
            System.out.print("    " + e.getKey() + " -> ");
            for (Edge edge : e.getValue())
                System.out.print(edge.to + "(w=" + edge.weight + ") ");
            System.out.println();
        }
    }
}
