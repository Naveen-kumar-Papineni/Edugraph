package main;

import core.*;
import trees.*;
import graphs.*;
import sorting.*;
import greedy.*;

import java.io.*;
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static List<Course>  courses  = new ArrayList<>();
    static List<Student> students = new ArrayList<>();

    // default data folder (relative to working directory)
    static final String DATA_DIR = "data";

    // ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        sep("EDUGRAPH - Adaptive Learning Platform");
        System.out.println("  Data can be loaded from CSV files or entered manually.");
        System.out.println("  Default CSV folder: " + new File(DATA_DIR).getAbsolutePath());

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Your choice: ");
            switch (choice) {
                case 1  -> csvImportMenu();
                case 2  -> manageCoursesMenu();
                case 3  -> manageStudentsMenu();
                case 4  -> runModule1_BST_AVL();
                case 5  -> runModule2_SegFenwick();
                case 6  -> runModule3_Graph();
                case 7  -> runModule4_DijkstraTopo();
                case 8  -> runModule5_Sorting();
                case 9  -> runModule6_GreedyDP();
                case 10 -> runModule7_ActivityScheduler();
                case 0  -> { System.out.println("\n  Goodbye!"); running = false; }
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    // ════════════════════════════════════════════════════════════
    //  MAIN MENU
    // ════════════════════════════════════════════════════════════
    static void printMainMenu() {
        sep("MAIN MENU");
        System.out.println("  [1]  Import Data from CSV");
        System.out.println("  [2]  Manage Courses  (manual)");
        System.out.println("  [3]  Manage Students (manual)");
        System.out.println("  [4]  Module 1  – BST & AVL Trees");
        System.out.println("  [5]  Module 2  – Segment Tree & Fenwick Tree");
        System.out.println("  [6]  Module 3  – Graph (BFS / DFS / Cycle Detection)");
        System.out.println("  [7]  Module 4  – Dijkstra & Topological Sort");
        System.out.println("  [8]  Module 5  – Sorting Algorithms");
        System.out.println("  [9]  Module 6  – Knapsack & LIS Progress Tracker");
        System.out.println("  [10] Module 7  – Activity Selector (Schedule)");
        System.out.println("  [0]  Exit");
        System.out.println();
        System.out.printf("  Loaded: %d course(s), %d student(s)%n", courses.size(), students.size());
    }

    // ════════════════════════════════════════════════════════════
    //  CSV IMPORT MENU
    // ════════════════════════════════════════════════════════════
    static void csvImportMenu() {
        boolean back = false;
        while (!back) {
            sep("CSV IMPORT");
            System.out.println("  CSV format (place files in the 'data/' folder or give full path):");
            System.out.println("  courses.csv  : courseId,name,credits,difficulty,popularity,prerequisites");
            System.out.println("  students.csv : rollNumber,name,enrolledCourses");
            System.out.println("  results.csv  : rollNumber,assessmentId,marksObtained,maxMarks");
            System.out.println("  sessions.csv : name,startHour,endHour");
            System.out.println("  (prerequisites and enrolledCourses are pipe-separated, e.g. C101|C102)");
            System.out.println();
            System.out.println("  [1] Load ALL from default folder (data/)");
            System.out.println("  [2] Load courses.csv  (custom path)");
            System.out.println("  [3] Load students.csv (custom path)");
            System.out.println("  [4] Load results.csv  (custom path)");
            System.out.println("  [5] Show current data summary");
            System.out.println("  [6] Clear all loaded data");
            System.out.println("  [0] Back");
            int c = readInt("Choice: ");
            switch (c) {
                case 1 -> loadAllDefault();
                case 2 -> loadCoursesFromPath();
                case 3 -> loadStudentsFromPath();
                case 4 -> loadResultsFromPath();
                case 5 -> showDataSummary();
                case 6 -> clearData();
                case 0 -> back = true;
                default -> System.out.println("  Invalid.");
            }
        }
    }

    static void loadAllDefault() {
        System.out.println();
        boolean anythingLoaded = false;

        String coursesPath  = DATA_DIR + File.separator + "courses.csv";
        String studentsPath = DATA_DIR + File.separator + "students.csv";
        String resultsPath  = DATA_DIR + File.separator + "results.csv";

        // Courses
        if (new File(coursesPath).exists()) {
            try {
                List<Course> loaded = CSVLoader.loadCourses(coursesPath);
                mergeCoursesIn(loaded);
                System.out.println("  [OK] courses.csv  → " + loaded.size() + " course(s) loaded.");
                anythingLoaded = true;
            } catch (IOException e) {
                System.out.println("  [ERR] courses.csv: " + e.getMessage());
            }
        } else {
            System.out.println("  [SKIP] " + coursesPath + " not found.");
        }

        // Students
        if (new File(studentsPath).exists()) {
            try {
                List<Student> loaded = CSVLoader.loadStudents(studentsPath);
                mergeStudentsIn(loaded);
                System.out.println("  [OK] students.csv → " + loaded.size() + " student(s) loaded.");
                anythingLoaded = true;
            } catch (IOException e) {
                System.out.println("  [ERR] students.csv: " + e.getMessage());
            }
        } else {
            System.out.println("  [SKIP] " + studentsPath + " not found.");
        }

        // Results (needs students already loaded)
        if (new File(resultsPath).exists()) {
            if (students.isEmpty()) {
                System.out.println("  [SKIP] results.csv skipped – no students loaded yet.");
            } else {
                try {
                    int count = CSVLoader.loadResults(resultsPath, students);
                    System.out.println("  [OK] results.csv  → " + count + " result(s) attached.");
                    anythingLoaded = true;
                } catch (IOException e) {
                    System.out.println("  [ERR] results.csv: " + e.getMessage());
                }
            }
        } else {
            System.out.println("  [SKIP] " + resultsPath + " not found.");
        }

        if (anythingLoaded) showDataSummary();
    }

    static void loadCoursesFromPath() {
        String path = promptPath("courses.csv", "courses.csv");
        try {
            List<Course> loaded = CSVLoader.loadCourses(path);
            mergeCoursesIn(loaded);
            System.out.println("  Loaded " + loaded.size() + " course(s) from " + path);
        } catch (IOException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    static void loadStudentsFromPath() {
        String path = promptPath("students.csv", "students.csv");
        try {
            List<Student> loaded = CSVLoader.loadStudents(path);
            mergeStudentsIn(loaded);
            System.out.println("  Loaded " + loaded.size() + " student(s) from " + path);
        } catch (IOException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    static void loadResultsFromPath() {
        if (students.isEmpty()) { System.out.println("  Load students first."); return; }
        String path = promptPath("results.csv", "results.csv");
        try {
            int count = CSVLoader.loadResults(path, students);
            System.out.println("  Attached " + count + " result(s) from " + path);
        } catch (IOException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    static String promptPath(String label, String defaultName) {
        System.out.print("  Path to " + label + " [default: data/" + defaultName + "]: ");
        String p = sc.nextLine().trim();
        return p.isEmpty() ? DATA_DIR + File.separator + defaultName : p;
    }

    static void mergeCoursesIn(List<Course> loaded) {
        for (Course lc : loaded) {
            if (findCourse(lc.getCourseId()) == null) courses.add(lc);
            else System.out.println("  [SKIP] Course " + lc.getCourseId() + " already exists – not overwritten.");
        }
    }

    static void mergeStudentsIn(List<Student> loaded) {
        for (Student ls : loaded) {
            if (findStudent(ls.getRollNumber()) == null) students.add(ls);
            else System.out.println("  [SKIP] Student roll " + ls.getRollNumber() + " already exists – not overwritten.");
        }
    }

    static void showDataSummary() {
        sep("DATA SUMMARY");
        System.out.println("  Courses (" + courses.size() + "):");
        for (Course c : courses)
            System.out.printf("    %-6s %-30s credits=%-2d diff=%-2d pop=%d prereqs=%s%n",
                c.getCourseId(), c.getName(), c.getCredits(),
                c.getDifficulty(), c.getPopularity(), c.getPrerequisiteIds());

        System.out.println("\n  Students (" + students.size() + "):");
        for (Student s : students) {
            System.out.printf("    %-5d %-20s GPA=%.2f courses=%s%n",
                s.getRollNumber(), s.getName(), s.getGpa(), s.getEnrolledCourses());
            if (!s.getResults().isEmpty()) {
                for (Result r : s.getResults())
                    System.out.printf("           Result: %s  %d/%d (%.1f%%)%n",
                        r.getAssessmentId(), r.getMarksObtained(), r.getMaxMarks(), r.getPercentage());
            }
        }
    }

    static void clearData() {
        System.out.print("  This will delete all loaded data. Confirm? (yes/no): ");
        String ans = sc.nextLine().trim();
        if (ans.equalsIgnoreCase("yes")) {
            courses.clear(); students.clear();
            System.out.println("  All data cleared.");
        } else {
            System.out.println("  Cancelled.");
        }
    }

    // ════════════════════════════════════════════════════════════
    //  COURSE MANAGEMENT (manual)
    // ════════════════════════════════════════════════════════════
    static void manageCoursesMenu() {
        boolean back = false;
        while (!back) {
            sep("COURSE MANAGEMENT");
            System.out.println("  [1] Add Course");
            System.out.println("  [2] Add Prerequisite");
            System.out.println("  [3] Increment Course Popularity");
            System.out.println("  [4] List All Courses");
            System.out.println("  [0] Back");
            int c = readInt("Choice: ");
            switch (c) {
                case 1 -> addCourse();
                case 2 -> addPrerequisite();
                case 3 -> incrementPopularity();
                case 4 -> listCourses();
                case 0 -> back = true;
                default -> System.out.println("  Invalid.");
            }
        }
    }

    static void addCourse() {
        System.out.print("  Course ID (e.g. C101): "); String id = sc.nextLine().trim();
        if (findCourse(id) != null) { System.out.println("  Course already exists."); return; }
        System.out.print("  Course Name: "); String name = sc.nextLine().trim();
        int credits    = readInt("Credits: ");
        int difficulty = readInt("Difficulty (1-10): ");
        courses.add(new Course(id, name, credits, difficulty));
        System.out.println("  Added: " + id + " - " + name);
    }

    static void addPrerequisite() {
        if (courses.size() < 2) { System.out.println("  Need at least 2 courses."); return; }
        listCourses();
        System.out.print("  Course ID to add prerequisite TO: "); String to  = sc.nextLine().trim();
        System.out.print("  Prerequisite Course ID:           "); String pre = sc.nextLine().trim();
        Course c = findCourse(to);
        if (c == null) { System.out.println("  Course not found."); return; }
        if (findCourse(pre) == null) { System.out.println("  Prerequisite course not found."); return; }
        c.addPrerequisite(pre);
        System.out.println("  Added prerequisite: " + pre + " -> " + to);
    }

    static void incrementPopularity() {
        listCourses();
        System.out.print("  Course ID: "); String id = sc.nextLine().trim();
        Course c = findCourse(id);
        if (c == null) { System.out.println("  Not found."); return; }
        int times = readInt("How many times to increment: ");
        for (int i = 0; i < times; i++) c.incrementPopularity();
        System.out.println("  Popularity of " + id + " is now " + c.getPopularity());
    }

    static void listCourses() {
        if (courses.isEmpty()) { System.out.println("  No courses yet."); return; }
        System.out.println("  Courses:");
        for (Course c : courses)
            System.out.printf("    %-6s %-30s credits=%-2d diff=%-2d pop=%d prereqs=%s%n",
                c.getCourseId(), c.getName(), c.getCredits(),
                c.getDifficulty(), c.getPopularity(), c.getPrerequisiteIds());
    }

    // ════════════════════════════════════════════════════════════
    //  STUDENT MANAGEMENT (manual)
    // ════════════════════════════════════════════════════════════
    static void manageStudentsMenu() {
        boolean back = false;
        while (!back) {
            sep("STUDENT MANAGEMENT");
            System.out.println("  [1] Add Student");
            System.out.println("  [2] Enrol Student in Course");
            System.out.println("  [3] Add Result for Student");
            System.out.println("  [4] List All Students");
            System.out.println("  [0] Back");
            int c = readInt("Choice: ");
            switch (c) {
                case 1 -> addStudent();
                case 2 -> enrolStudent();
                case 3 -> addResult();
                case 4 -> listStudents();
                case 0 -> back = true;
                default -> System.out.println("  Invalid.");
            }
        }
    }

    static void addStudent() {
        int roll = readInt("Roll Number: ");
        if (findStudent(roll) != null) { System.out.println("  Roll already exists."); return; }
        System.out.print("  Student Name: "); String name = sc.nextLine().trim();
        students.add(new Student(roll, name, 0));
        System.out.println("  Added: " + roll + " - " + name);
    }

    static void enrolStudent() {
        if (students.isEmpty()) { System.out.println("  No students yet."); return; }
        listStudents();
        int roll = readInt("Student Roll Number: ");
        Student s = findStudent(roll);
        if (s == null) { System.out.println("  Not found."); return; }
        listCourses();
        System.out.print("  Course ID: "); String cid = sc.nextLine().trim();
        if (findCourse(cid) == null) { System.out.println("  Course not found."); return; }
        s.enrol(cid);
        System.out.println("  " + s.getName() + " enrolled in " + cid);
    }

    static void addResult() {
        if (students.isEmpty()) { System.out.println("  No students yet."); return; }
        listStudents();
        int roll = readInt("Student Roll Number: ");
        Student s = findStudent(roll);
        if (s == null) { System.out.println("  Student not found."); return; }
        System.out.print("  Assessment ID (e.g. A1): "); String aid = sc.nextLine().trim();
        int obtained = readInt("Marks Obtained: ");
        int maxMarks = readInt("Max Marks: ");
        s.addResult(new Result(roll, aid, obtained, maxMarks));
        System.out.printf("  Result added. New GPA: %.2f%n", s.getGpa());
    }

    static void listStudents() {
        if (students.isEmpty()) { System.out.println("  No students yet."); return; }
        System.out.println("  Students:");
        for (Student s : students)
            System.out.printf("    %-5d %-20s GPA=%.2f  courses=%s%n",
                s.getRollNumber(), s.getName(), s.getGpa(), s.getEnrolledCourses());
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 1 – BST & AVL
    // ════════════════════════════════════════════════════════════
    static void runModule1_BST_AVL() {
        sep("MODULE 1 - BST & AVL Trees");
        if (courses.isEmpty() || students.isEmpty()) { notReady(); return; }

        System.out.println("\n  -- BST_CourseIndex --");
        BST_CourseIndex bst = new BST_CourseIndex();
        for (Course c : courses) bst.insert(c);
        bst.inorder();

        System.out.print("  Search course ID: "); String sid = sc.nextLine().trim();
        System.out.println("  Search result: " + bst.search(sid));

        System.out.print("  Delete course ID (blank to skip): "); String del = sc.nextLine().trim();
        if (!del.isEmpty()) { bst.delete(del); System.out.println("  After deletion:"); bst.inorder(); }

        System.out.println("\n  -- AVL_StudentDB --");
        AVL_StudentDB avl = new AVL_StudentDB();
        for (Student s : students) avl.insert(s);
        avl.inorder();

        int roll = readInt("Search roll number: ");
        System.out.println("  Search result: " + avl.search(roll));
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 2 – SEGMENT TREE & FENWICK TREE
    // ════════════════════════════════════════════════════════════
    static void runModule2_SegFenwick() {
        sep("MODULE 2 - Segment Tree & Fenwick Tree");

        int[] scores = null;

        // Offer to build score array from loaded student results
        if (!students.isEmpty()) {
            System.out.println("  [1] Build score array from loaded student results");
            System.out.println("  [2] Enter scores manually (comma-separated)");
            int pick = readInt("Choice: ");
            if (pick == 1) scores = buildScoreArrayFromStudents();
        }

        if (scores == null) {
            System.out.println("  Enter scores (comma-separated, e.g. 78,92,55,95,60):");
            System.out.print("  > ");
            scores = parseIntArray(sc.nextLine().trim());
        }

        if (scores == null || scores.length == 0) { System.out.println("  Invalid input."); return; }
        int n = scores.length;
        System.out.println("  Score array: " + Arrays.toString(scores));

        System.out.println("\n  -- SegmentTree_Analytics --");
        SegmentTree_Analytics seg = new SegmentTree_Analytics(scores);
        int l1 = readInt("Range query L (0-based): ");
        int r1 = readInt("Range query R (0-based, inclusive): ");
        if (l1 < 0 || r1 >= n || l1 > r1) { System.out.println("  Invalid range."); }
        else {
            System.out.println("  Sum     [" + l1 + "," + r1 + "]: " + seg.rangeSum(l1, r1));
            System.out.println("  Min     [" + l1 + "," + r1 + "]: " + seg.rangeMin(l1, r1));
            System.out.println("  Max     [" + l1 + "," + r1 + "]: " + seg.rangeMax(l1, r1));
            System.out.printf( "  Average [0,%d]: %.1f%n", n-1, seg.rangeAverage(0, n-1));
        }

        System.out.println("\n  -- FenwickTree_Scores --");
        FenwickTree_Scores fen = new FenwickTree_Scores(n);
        fen.build(scores);
        fen.printTree();
        int ps = readInt("Prefix sum up to index (1-based): ");
        if (ps >= 1 && ps <= n) System.out.println("  Prefix sum [1.." + ps + "]: " + fen.prefixSum(ps));
        int rl = readInt("Range sum L (1-based): ");
        int rr = readInt("Range sum R (1-based): ");
        if (rl >= 1 && rr <= n && rl <= rr)
            System.out.println("  Range sum [" + rl + ".." + rr + "]: " + fen.rangeSum(rl, rr));
        int upPos   = readInt("Update position (1-based): ");
        int upDelta = readInt("Update delta value: ");
        if (upPos >= 1 && upPos <= n) {
            fen.update(upPos, upDelta);
            System.out.println("  After update, prefix [1.." + ps + "]: " + fen.prefixSum(ps));
        }
    }

    static int[] buildScoreArrayFromStudents() {
        List<Integer> scores = new ArrayList<>();
        for (Student s : students)
            for (Result r : s.getResults())
                scores.add(r.getMarksObtained());
        if (scores.isEmpty()) { System.out.println("  No results found in loaded students."); return null; }
        return scores.stream().mapToInt(Integer::intValue).toArray();
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 3 – GRAPH
    // ════════════════════════════════════════════════════════════
    static void runModule3_Graph() {
        sep("MODULE 3 - Course Graph, BFS, DFS");
        if (courses.isEmpty()) { notReady(); return; }

        CourseGraph graph = buildCourseGraph();

        System.out.println("\n  -- BFS_PathExplorer --");
        BFS_PathExplorer bfs = new BFS_PathExplorer(graph);
        System.out.print("  BFS start course ID: "); String bfsStart = sc.nextLine().trim();
        if (findCourse(bfsStart) != null)
            System.out.println("  BFS traversal: " + bfs.bfsTraversal(bfsStart));
        System.out.print("  Shortest path FROM: "); String from = sc.nextLine().trim();
        System.out.print("  Shortest path TO:   "); String to   = sc.nextLine().trim();
        if (findCourse(from) != null && findCourse(to) != null)
            System.out.println("  Path: " + bfs.shortestHopPath(from, to));

        System.out.println("\n  -- DFS_CycleDetector --");
        DFS_CycleDetector dfs = new DFS_CycleDetector(graph);
        System.out.print("  DFS start course ID: "); String dfsStart = sc.nextLine().trim();
        if (findCourse(dfsStart) != null)
            System.out.println("  DFS traversal: " + dfs.dfsTraversal(dfsStart));
        System.out.println("  Has cycle? " + dfs.hasCycle());
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 4 – DIJKSTRA & TOPO SORT
    // ════════════════════════════════════════════════════════════
    static void runModule4_DijkstraTopo() {
        sep("MODULE 4 - Dijkstra & Topological Sort");
        if (courses.isEmpty()) { notReady(); return; }

        CourseGraph graph = buildCourseGraph();

        System.out.println("\n  -- Dijkstra_LearningPath --");
        Dijkstra_LearningPath dijk = new Dijkstra_LearningPath(graph);
        System.out.print("  Source course ID: "); String src = sc.nextLine().trim();
        if (findCourse(src) != null) {
            System.out.println("  Distances: " + dijk.computeDistances(src));
            System.out.print("  Destination: "); String dst = sc.nextLine().trim();
            if (findCourse(dst) != null)
                System.out.println("  Path: " + dijk.getShortestPath(src, dst));
        }

        System.out.println("\n  -- TopoSort_CourseOrder --");
        System.out.println("  Valid completion order: " + new TopoSort_CourseOrder(graph).sort());
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 5 – SORTING
    // ════════════════════════════════════════════════════════════
    static void runModule5_Sorting() {
        sep("MODULE 5 - Sorting Algorithms");
        if (courses.isEmpty() || students.isEmpty()) { notReady(); return; }

        System.out.println("\n  -- MergeSort_StudentRank (by GPA desc) --");
        for (Student s : new MergeSort_StudentRank().sort(students))
            System.out.printf("    %-20s GPA=%.2f%n", s.getName(), s.getGpa());

        System.out.println("\n  -- HeapSort_TopStudents --");
        int k = readInt("How many top students to show? ");
        for (Student s : new HeapSort_TopStudents().topK(students, Math.min(k, students.size())))
            System.out.printf("    %-20s GPA=%.2f%n", s.getName(), s.getGpa());

        System.out.println("\n  -- QuickSort_CourseRank (by popularity desc) --");
        List<Course> cl = new ArrayList<>(courses);
        new QuickSort_CourseRank().sort(cl);
        for (Course c : cl)
            System.out.printf("    %-6s %-30s pop=%d%n", c.getCourseId(), c.getName(), c.getPopularity());

        System.out.println("\n  -- RadixSort_RollNumbers --");
        int[] rolls = students.stream().mapToInt(Student::getRollNumber).toArray();
        System.out.println("  Before: " + Arrays.toString(rolls));
        new RadixSort_RollNumbers().sort(rolls);
        System.out.println("  After:  " + Arrays.toString(rolls));
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 6 – KNAPSACK & LIS
    // ════════════════════════════════════════════════════════════
    static void runModule6_GreedyDP() {
        sep("MODULE 6 - Knapsack & LIS Progress Tracker");
        if (courses.isEmpty()) { notReady(); return; }

        System.out.println("\n  -- Knapsack_CourseSelector --");
        int creditLimit = readInt("Credit limit: ");
        Knapsack_CourseSelector ks = new Knapsack_CourseSelector();
        System.out.println("  Selected (0/1 Knapsack): " + ks.zeroOneKnapsack(courses, creditLimit));
        ks.fractionalKnapsack(courses, creditLimit);

        System.out.println("\n  -- LIS_ProgressTracker --");
        if (students.isEmpty()) { System.out.println("  Load students first."); return; }
        listStudents();
        int roll = readInt("Student roll number to analyse: ");
        Student s = findStudent(roll);
        if (s == null) { System.out.println("  Not found."); return; }
        if (s.getResults().isEmpty()) { System.out.println("  No results for this student."); return; }
        new LIS_ProgressTracker().analyseStudent(s.getName(), s.getResults());
    }

    // ════════════════════════════════════════════════════════════
    //  MODULE 7 – ACTIVITY SELECTOR
    // ════════════════════════════════════════════════════════════
    static void runModule7_ActivityScheduler() {
        sep("MODULE 7 - Activity Selector (Schedule)");

        List<ActivitySelector_Schedule.StudySession> sessions = new ArrayList<>();

        System.out.println("  [1] Load sessions from data/sessions.csv");
        System.out.println("  [2] Load sessions from custom CSV path");
        System.out.println("  [3] Enter sessions manually");
        int pick = readInt("Choice: ");

        if (pick == 1 || pick == 2) {
            String path = (pick == 1)
                ? DATA_DIR + File.separator + "sessions.csv"
                : promptPath("sessions.csv", "sessions.csv");
            try {
                sessions = CSVLoader.loadSessions(path);
                System.out.println("  Loaded " + sessions.size() + " session(s) from " + path);
            } catch (IOException e) {
                System.out.println("  Error reading file: " + e.getMessage());
            }
        } else {
            System.out.println("  Enter sessions as: name,startHour,endHour  (type 'done' to finish)");
            while (true) {
                System.out.print("  Session: ");
                String line = sc.nextLine().trim();
                if (line.equalsIgnoreCase("done")) break;
                String[] p = line.split(",");
                if (p.length != 3) { System.out.println("  Bad format."); continue; }
                try {
                    int s = Integer.parseInt(p[1].trim()), e = Integer.parseInt(p[2].trim());
                    if (s >= e) { System.out.println("  Start must be before end."); continue; }
                    sessions.add(new ActivitySelector_Schedule.StudySession(p[0].trim(), s, e));
                } catch (NumberFormatException ex) { System.out.println("  Invalid numbers."); }
            }
        }

        if (sessions.isEmpty()) { System.out.println("  No sessions to schedule."); return; }
        ActivitySelector_Schedule act = new ActivitySelector_Schedule();
        act.printSchedule(act.selectOptimalSessions(sessions));
    }

    // ════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════
    static CourseGraph buildCourseGraph() {
        CourseGraph graph = new CourseGraph();
        for (Course c : courses) graph.addCourse(c);
        for (Course c : courses)
            for (String pre : c.getPrerequisiteIds())
                graph.addPrerequisite(pre, c.getCourseId());
        graph.printGraph();
        return graph;
    }

    static Course  findCourse(String id)  { for (Course c  : courses)  if (c.getCourseId().equalsIgnoreCase(id)) return c; return null; }
    static Student findStudent(int roll)  { for (Student s : students) if (s.getRollNumber() == roll)            return s; return null; }
    static void    notReady()             { System.out.println("  Please load or add courses and students first (options 1–3)."); }

    static int readInt(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  Please enter a valid integer."); }
        }
    }

    static int[] parseIntArray(String csv) {
        try {
            String[] parts = csv.split(",");
            int[] arr = new int[parts.length];
            for (int i = 0; i < parts.length; i++) arr[i] = Integer.parseInt(parts[i].trim());
            return arr;
        } catch (NumberFormatException e) { return null; }
    }

    static void sep(String title) {
        System.out.println("\n" + "=".repeat(55));
        System.out.println("  " + title);
        System.out.println("=".repeat(55));
    }
}
