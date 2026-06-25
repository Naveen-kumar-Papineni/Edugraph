package api;

import core.*;
import trees.*;
import graphs.*;
import sorting.*;
import greedy.*;
import main.CSVLoader;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ApiServer {

    static List<Course> courses = new ArrayList<>();
    static List<Student> students = new ArrayList<>();
    static final String DATA_DIR = "data";
    static final String WEB_DIR = "web";

    public static void main(String[] args) throws IOException {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Static frontend
        server.createContext("/", new StaticHandler());

        // Data endpoints
        server.createContext("/api/data/load-default", cors(ApiServer::loadDefault));
        server.createContext("/api/data/summary", cors(ApiServer::summary));
        server.createContext("/api/data/clear", cors(ApiServer::clearData));
        server.createContext("/api/courses", cors(ApiServer::listCourses));
        server.createContext("/api/students", cors(ApiServer::listStudents));

        // Tree endpoints (Module 1)
        server.createContext("/api/bst/search", cors(ApiServer::bstSearch));
        server.createContext("/api/bst/inorder", cors(ApiServer::bstInorder));
        server.createContext("/api/avl/search", cors(ApiServer::avlSearch));
        server.createContext("/api/avl/inorder", cors(ApiServer::avlInorder));

        // Segment / Fenwick (Module 2)
        server.createContext("/api/segment", cors(ApiServer::segmentTree));
        server.createContext("/api/fenwick", cors(ApiServer::fenwickTree));

        // Graph (Module 3 & 4)
        server.createContext("/api/graph", cors(ApiServer::graphData));
        server.createContext("/api/bfs", cors(ApiServer::bfs));
        server.createContext("/api/dfs", cors(ApiServer::dfs));
        server.createContext("/api/cycle", cors(ApiServer::cycle));
        server.createContext("/api/dijkstra", cors(ApiServer::dijkstra));
        server.createContext("/api/toposort", cors(ApiServer::topoSort));

        // Sorting (Module 5)
        server.createContext("/api/sort/students", cors(ApiServer::sortStudents));
        server.createContext("/api/sort/topstudents", cors(ApiServer::topStudents));
        server.createContext("/api/sort/courses", cors(ApiServer::sortCourses));
        server.createContext("/api/sort/rollnumbers", cors(ApiServer::sortRollNumbers));

        // Knapsack & LIS (Module 6)
        server.createContext("/api/knapsack", cors(ApiServer::knapsack));
        server.createContext("/api/lis", cors(ApiServer::lis));

        // Activity Scheduler (Module 7)
        server.createContext("/api/schedule", cors(ApiServer::schedule));

        server.setExecutor(null);
        server.start();
        System.out.println("EduGraph API server running at http://localhost:" + port + "/");
        System.out.println("Data folder: " + new File(DATA_DIR).getAbsolutePath());
    }

    // ════════════════════════════════════════════════════════════
    //  STATIC FILE SERVING
    // ════════════════════════════════════════════════════════════
    static class StaticHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            String path = ex.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            Path file = Paths.get(WEB_DIR, path.substring(1)).normalize();
            if (!file.startsWith(Paths.get(WEB_DIR)) || !Files.exists(file) || Files.isDirectory(file)) {
                byte[] msg = "Not found".getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(404, msg.length);
                ex.getResponseBody().write(msg);
                ex.close();
                return;
            }
            byte[] data = Files.readAllBytes(file);
            String ct = "text/plain";
            String name = file.toString();
            if (name.endsWith(".html")) ct = "text/html; charset=utf-8";
            else if (name.endsWith(".js")) ct = "application/javascript";
            else if (name.endsWith(".css")) ct = "text/css";
            ex.getResponseHeaders().add("Content-Type", ct);
            ex.sendResponseHeaders(200, data.length);
            ex.getResponseBody().write(data);
            ex.close();
        }
    }

    // ════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════
    interface Action { void run(HttpExchange ex) throws IOException; }

    static HttpHandler cors(Action action) {
        return ex -> {
            ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                ex.sendResponseHeaders(204, -1);
                return;
            }
            try {
                action.run(ex);
            } catch (Exception e) {
                sendJson(ex, 500, Json.obj("error", e.toString()));
            }
        };
    }

    static Map<String, String> queryParams(HttpExchange ex) {
        Map<String, String> map = new LinkedHashMap<>();
        String q = ex.getRequestURI().getQuery();
        if (q == null) return map;
        for (String pair : q.split("&")) {
            int idx = pair.indexOf('=');
            if (idx < 0) continue;
            try {
                String k = java.net.URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                String v = java.net.URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                map.put(k, v);
            } catch (Exception ignored) {}
        }
        return map;
    }

    static String readBody(HttpExchange ex) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = ex.getRequestBody();
        byte[] buf = new byte[4096];
        int r;
        while ((r = is.read(buf)) != -1) bos.write(buf, 0, r);
        return bos.toString(StandardCharsets.UTF_8);
    }

    static void sendJson(HttpExchange ex, int status, Object payload) throws IOException {
        byte[] data = Json.write(payload).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, data.length);
        ex.getResponseBody().write(data);
        ex.close();
    }

    static Course findCourse(String id) {
        for (Course c : courses) if (c.getCourseId().equalsIgnoreCase(id)) return c;
        return null;
    }

    static Student findStudent(int roll) {
        for (Student s : students) if (s.getRollNumber() == roll) return s;
        return null;
    }

    static Map<String, Object> courseToMap(Course c) {
        return Json.obj(
            "courseId", c.getCourseId(),
            "name", c.getName(),
            "credits", c.getCredits(),
            "difficulty", c.getDifficulty(),
            "popularity", c.getPopularity(),
            "prerequisites", c.getPrerequisiteIds()
        );
    }

    static Map<String, Object> studentToMap(Student s) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (Result r : s.getResults())
            results.add(Json.obj(
                "assessmentId", r.getAssessmentId(),
                "marksObtained", r.getMarksObtained(),
                "maxMarks", r.getMaxMarks(),
                "percentage", Math.round(r.getPercentage() * 10) / 10.0
            ));
        return Json.obj(
            "rollNumber", s.getRollNumber(),
            "name", s.getName(),
            "gpa", Math.round(s.getGpa() * 100) / 100.0,
            "enrolledCourses", s.getEnrolledCourses(),
            "results", results
        );
    }

    static CourseGraph buildCourseGraph() {
        CourseGraph graph = new CourseGraph();
        for (Course c : courses) graph.addCourse(c);
        for (Course c : courses)
            for (String pre : c.getPrerequisiteIds())
                graph.addPrerequisite(pre, c.getCourseId());
        return graph;
    }

    // ════════════════════════════════════════════════════════════
    //  DATA ENDPOINTS
    // ════════════════════════════════════════════════════════════
    static void loadDefault(HttpExchange ex) throws IOException {
        List<String> log = new ArrayList<>();
        String coursesPath = DATA_DIR + File.separator + "courses.csv";
        String studentsPath = DATA_DIR + File.separator + "students.csv";
        String resultsPath = DATA_DIR + File.separator + "results.csv";

        try {
            if (new File(coursesPath).exists()) {
                List<Course> loaded = CSVLoader.loadCourses(coursesPath);
                mergeCoursesIn(loaded);
                log.add("Loaded " + loaded.size() + " course(s) from courses.csv");
            } else log.add("courses.csv not found");

            if (new File(studentsPath).exists()) {
                List<Student> loaded = CSVLoader.loadStudents(studentsPath);
                mergeStudentsIn(loaded);
                log.add("Loaded " + loaded.size() + " student(s) from students.csv");
            } else log.add("students.csv not found");

            if (new File(resultsPath).exists() && !students.isEmpty()) {
                int count = CSVLoader.loadResults(resultsPath, students);
                log.add("Attached " + count + " result(s) from results.csv");
            } else if (new File(resultsPath).exists()) {
                log.add("results.csv skipped - no students loaded");
            } else log.add("results.csv not found");
        } catch (IOException e) {
            log.add("Error: " + e.getMessage());
        }

        sendJson(ex, 200, Json.obj("log", log, "courses", courses.size(), "students", students.size()));
    }

    static void mergeCoursesIn(List<Course> loaded) {
        for (Course c : loaded) {
            if (findCourse(c.getCourseId()) == null) courses.add(c);
        }
    }

    static void mergeStudentsIn(List<Student> loaded) {
        for (Student s : loaded) {
            if (findStudent(s.getRollNumber()) == null) students.add(s);
        }
    }

    static void clearData(HttpExchange ex) throws IOException {
        courses.clear();
        students.clear();
        sendJson(ex, 200, Json.obj("status", "cleared"));
    }

    static void summary(HttpExchange ex) throws IOException {
        sendJson(ex, 200, Json.obj("courses", courses.size(), "students", students.size()));
    }

    static void listCourses(HttpExchange ex) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Course c : courses) list.add(courseToMap(c));
        sendJson(ex, 200, list);
    }

    static void listStudents(HttpExchange ex) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Student s : students) list.add(studentToMap(s));
        sendJson(ex, 200, list);
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 1 - BST & AVL
    // ════════════════════════════════════════════════════════════
    static void bstInorder(HttpExchange ex) throws IOException {
        BST_CourseIndex bst = new BST_CourseIndex();
        for (Course c : courses) bst.insert(c);
        List<Map<String, Object>> sortedCourses = new ArrayList<>();
        List<Course> sorted = new ArrayList<>(courses);
        sorted.sort(Comparator.comparing(Course::getCourseId));
        for (Course c : sorted) sortedCourses.add(courseToMap(c));
        sendJson(ex, 200, sortedCourses);
    }

    static void bstSearch(HttpExchange ex) throws IOException {
        Map<String, String> q = queryParams(ex);
        BST_CourseIndex bst = new BST_CourseIndex();
        for (Course c : courses) bst.insert(c);
        Course found = bst.search(q.getOrDefault("id", ""));
        sendJson(ex, 200, found != null ? courseToMap(found) : Json.obj("found", false));
    }

    static void avlInorder(HttpExchange ex) throws IOException {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparingInt(Student::getRollNumber));
        List<Map<String, Object>> list = new ArrayList<>();
        for (Student s : sorted) list.add(studentToMap(s));
        sendJson(ex, 200, list);
    }

    static void avlSearch(HttpExchange ex) throws IOException {
        Map<String, String> q = queryParams(ex);
        AVL_StudentDB avl = new AVL_StudentDB();
        for (Student s : students) avl.insert(s);
        try {
            int roll = Integer.parseInt(q.getOrDefault("roll", "-1"));
            Student found = avl.search(roll);
            sendJson(ex, 200, found != null ? studentToMap(found) : Json.obj("found", false));
        } catch (NumberFormatException e) {
            sendJson(ex, 400, Json.obj("error", "invalid roll number"));
        }
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 2 - SEGMENT TREE & FENWICK TREE
    // ════════════════════════════════════════════════════════════
    static void segmentTree(HttpExchange ex) throws IOException {
        Map<String, String> q = queryParams(ex);
        int[] scores = students.stream().mapToInt(s -> (int) Math.round(s.getGpa() * 10)).toArray();
        if (scores.length == 0) { sendJson(ex, 200, Json.obj("error", "no students loaded")); return; }
        int l = Integer.parseInt(q.getOrDefault("l", "0"));
        int r = Integer.parseInt(q.getOrDefault("r", String.valueOf(scores.length - 1)));
        l = Math.max(0, l); r = Math.min(scores.length - 1, r);
        SegmentTree_Analytics seg = new SegmentTree_Analytics(scores);
        sendJson(ex, 200, Json.obj(
            "scores", scores,
            "rangeSum", seg.rangeSum(l, r),
            "rangeMin", seg.rangeMin(l, r),
            "rangeMax", seg.rangeMax(l, r),
            "rangeAverage", Math.round(seg.rangeAverage(l, r) * 100) / 100.0
        ));
    }

    static void fenwickTree(HttpExchange ex) throws IOException {
        Map<String, String> q = queryParams(ex);
        int[] scores = students.stream().mapToInt(s -> (int) Math.round(s.getGpa() * 10)).toArray();
        if (scores.length == 0) { sendJson(ex, 200, Json.obj("error", "no students loaded")); return; }
        FenwickTree_Scores fen = new FenwickTree_Scores(scores.length);
        fen.build(scores);
        int l = Integer.parseInt(q.getOrDefault("l", "1"));
        int r = Integer.parseInt(q.getOrDefault("r", String.valueOf(scores.length)));
        sendJson(ex, 200, Json.obj("scores", scores, "rangeSum", fen.rangeSum(l, r)));
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 3 & 4 - GRAPH
    // ════════════════════════════════════════════════════════════
    static void graphData(HttpExchange ex) throws IOException {
        CourseGraph graph = buildCourseGraph();
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();
        for (String id : graph.getAllCourseIds()) {
            Course c = graph.getCourse(id);
            nodes.add(Json.obj("id", id, "name", c.getName()));
            for (CourseGraph.Edge e : graph.getNeighbours(id))
                edges.add(Json.obj("from", id, "to", e.to, "weight", e.weight));
        }
        sendJson(ex, 200, Json.obj("nodes", nodes, "edges", edges));
    }

    static void bfs(HttpExchange ex) throws IOException {
        Map<String, String> q = queryParams(ex);
        CourseGraph graph = buildCourseGraph();
        BFS_PathExplorer explorer = new BFS_PathExplorer(graph);
        String start = q.get("start"), target = q.get("target");
        if (start == null || findCourse(start) == null) { sendJson(ex, 400, Json.obj("error", "invalid start course")); return; }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("traversal", explorer.bfsTraversal(start));
        if (target != null && findCourse(target) != null)
            result.put("shortestPath", explorer.shortestHopPath(start, target));
        sendJson(ex, 200, result);
    }

    static void dfs(HttpExchange ex) throws IOException {
        Map<String, String> q = queryParams(ex);
        CourseGraph graph = buildCourseGraph();
        DFS_CycleDetector detector = new DFS_CycleDetector(graph);
        String start = q.get("start");
        if (start == null || findCourse(start) == null) { sendJson(ex, 400, Json.obj("error", "invalid start course")); return; }
        sendJson(ex, 200, Json.obj("traversal", detector.dfsTraversal(start), "hasCycle", detector.hasCycle()));
    }

    static void cycle(HttpExchange ex) throws IOException {
        CourseGraph graph = buildCourseGraph();
        DFS_CycleDetector detector = new DFS_CycleDetector(graph);
        sendJson(ex, 200, Json.obj("hasCycle", detector.hasCycle()));
    }

    static void dijkstra(HttpExchange ex) throws IOException {
        Map<String, String> q = queryParams(ex);
        CourseGraph graph = buildCourseGraph();
        Dijkstra_LearningPath dijk = new Dijkstra_LearningPath(graph);
        String source = q.get("source"), target = q.get("target");
        if (source == null || findCourse(source) == null) { sendJson(ex, 400, Json.obj("error", "invalid source course")); return; }
        Map<String, Integer> distances = dijk.computeDistances(source);
        Map<String, Object> distMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : distances.entrySet())
            distMap.put(e.getKey(), e.getValue() == Integer.MAX_VALUE ? "unreachable" : e.getValue());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("distances", distMap);
        if (target != null && findCourse(target) != null)
            result.put("path", dijk.getShortestPath(source, target));
        sendJson(ex, 200, result);
    }

    static void topoSort(HttpExchange ex) throws IOException {
        CourseGraph graph = buildCourseGraph();
        List<String> order = new TopoSort_CourseOrder(graph).sort();
        sendJson(ex, 200, Json.obj("order", order, "valid", order.size() == graph.vertexCount()));
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 5 - SORTING
    // ════════════════════════════════════════════════════════════
    static void sortStudents(HttpExchange ex) throws IOException {
        List<Student> sorted = new MergeSort_StudentRank().sort(students);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Student s : sorted) list.add(studentToMap(s));
        sendJson(ex, 200, list);
    }

    static void topStudents(HttpExchange ex) throws IOException {
        Map<String, String> q = queryParams(ex);
        int k = Integer.parseInt(q.getOrDefault("k", "3"));
        k = Math.max(0, Math.min(k, students.size()));
        List<Student> top = new HeapSort_TopStudents().topK(students, k);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Student s : top) list.add(studentToMap(s));
        sendJson(ex, 200, list);
    }

    static void sortCourses(HttpExchange ex) throws IOException {
        List<Course> cl = new ArrayList<>(courses);
        new QuickSort_CourseRank().sort(cl);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Course c : cl) list.add(courseToMap(c));
        sendJson(ex, 200, list);
    }

    static void sortRollNumbers(HttpExchange ex) throws IOException {
        int[] before = students.stream().mapToInt(Student::getRollNumber).toArray();
        int[] after = before.clone();
        new RadixSort_RollNumbers().sort(after);
        sendJson(ex, 200, Json.obj("before", before, "after", after));
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 6 - KNAPSACK & LIS
    // ════════════════════════════════════════════════════════════
    static void knapsack(HttpExchange ex) throws IOException {
        Map<String, String> q = queryParams(ex);
        int creditLimit = Integer.parseInt(q.getOrDefault("creditLimit", "10"));
        Knapsack_CourseSelector ks = new Knapsack_CourseSelector();
        List<String> selected = ks.zeroOneKnapsack(courses, creditLimit);
        double fractionalValue = ks.fractionalKnapsack(courses, creditLimit);
        sendJson(ex, 200, Json.obj(
            "zeroOneSelected", selected,
            "fractionalMaxValue", Math.round(fractionalValue * 100) / 100.0
        ));
    }

    static void lis(HttpExchange ex) throws IOException {
        Map<String, String> q = queryParams(ex);
        try {
            int roll = Integer.parseInt(q.getOrDefault("roll", "-1"));
            Student s = findStudent(roll);
            if (s == null) { sendJson(ex, 404, Json.obj("error", "student not found")); return; }
            if (s.getResults().isEmpty()) { sendJson(ex, 200, Json.obj("error", "no results for this student")); return; }
            List<Integer> scores = new ArrayList<>();
            for (Result r : s.getResults()) scores.add((int) r.getPercentage());
            LIS_ProgressTracker tracker = new LIS_ProgressTracker();
            List<Integer> path = tracker.lisWithPath(scores);
            int fast = tracker.lisLengthFast(scores);
            boolean consistent = path.size() >= scores.size() * 0.6;
            sendJson(ex, 200, Json.obj(
                "name", s.getName(),
                "scores", scores,
                "longestImprovingStreak", path,
                "lisLength", path.size(),
                "lisLengthFast", fast,
                "consistentImprovement", consistent
            ));
        } catch (NumberFormatException e) {
            sendJson(ex, 400, Json.obj("error", "invalid roll number"));
        }
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 7 - ACTIVITY SCHEDULER
    // ════════════════════════════════════════════════════════════
    static void schedule(HttpExchange ex) throws IOException {
        List<ActivitySelector_Schedule.StudySession> sessions = new ArrayList<>();
        if (ex.getRequestMethod().equalsIgnoreCase("POST")) {
            String body = readBody(ex);
            for (Map<String, String> row : Json.parseArrayOfFlatObjects(body)) {
                try {
                    String name = row.get("name");
                    int start = Integer.parseInt(row.get("start"));
                    int end = Integer.parseInt(row.get("end"));
                    if (name != null && start < end) sessions.add(new ActivitySelector_Schedule.StudySession(name, start, end));
                } catch (Exception ignored) {}
            }
        } else {
            String path = DATA_DIR + File.separator + "sessions.csv";
            if (new File(path).exists()) {
                try { sessions = CSVLoader.loadSessions(path); } catch (IOException e) { /* ignore */ }
            }
        }
        if (sessions.isEmpty()) { sendJson(ex, 200, Json.obj("input", new ArrayList<>(), "selected", new ArrayList<>())); return; }

        List<Map<String, Object>> input = new ArrayList<>();
        for (ActivitySelector_Schedule.StudySession s : sessions)
            input.add(Json.obj("name", s.name, "start", s.start, "end", s.end));

        List<ActivitySelector_Schedule.StudySession> selected =
            new ActivitySelector_Schedule().selectOptimalSessions(sessions);
        List<Map<String, Object>> selectedList = new ArrayList<>();
        for (ActivitySelector_Schedule.StudySession s : selected)
            selectedList.add(Json.obj("name", s.name, "start", s.start, "end", s.end));

        sendJson(ex, 200, Json.obj("input", input, "selected", selectedList));
    }
}
