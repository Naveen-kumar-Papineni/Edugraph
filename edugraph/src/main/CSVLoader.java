package main;

import core.*;
import greedy.ActivitySelector_Schedule;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * CSVLoader – reads courses, students, results and study-sessions from CSV files.
 *
 * Expected file formats
 * ─────────────────────
 * courses.csv
 *   courseId,name,credits,difficulty,popularity,prerequisites
 *   C101,Intro to Programming,3,2,3,
 *   C102,Data Structures,4,5,2,C101
 *   C105,Machine Learning,4,9,4,C103|C104   ← multiple prereqs separated by |
 *
 * students.csv
 *   rollNumber,name,enrolledCourses
 *   101,Arjun Sharma,C101|C102
 *
 * results.csv
 *   rollNumber,assessmentId,marksObtained,maxMarks
 *   101,A1,78,100
 *
 * sessions.csv
 *   name,startHour,endHour
 *   Morning Lecture,8,10
 */
public class CSVLoader {

    // ── Public load methods ──────────────────────────────────────

    public static List<Course> loadCourses(String filePath) throws IOException {
        List<String[]> rows = readCSV(filePath);
        List<Course> list = new ArrayList<>();

        for (String[] row : rows) {
            if (row.length < 4) { warn("courses.csv", row); continue; }
            try {
                String id         = row[0].trim();
                String name       = row[1].trim();
                int    credits    = Integer.parseInt(row[2].trim());
                int    difficulty = Integer.parseInt(row[3].trim());
                int    popularity = (row.length > 4 && !row[4].trim().isEmpty())
                                    ? Integer.parseInt(row[4].trim()) : 0;

                Course c = new Course(id, name, credits, difficulty);
                for (int i = 0; i < popularity; i++) c.incrementPopularity();

                // prerequisites column (index 5), pipe-separated
                if (row.length > 5 && !row[5].trim().isEmpty()) {
                    for (String pre : row[5].trim().split("\\|"))
                        c.addPrerequisite(pre.trim());
                }
                list.add(c);
            } catch (NumberFormatException e) {
                System.out.println("  [CSVLoader] Skipping bad courses row: " + Arrays.toString(row));
            }
        }
        return list;
    }

    public static List<Student> loadStudents(String filePath) throws IOException {
        List<String[]> rows = readCSV(filePath);
        List<Student> list = new ArrayList<>();

        for (String[] row : rows) {
            if (row.length < 2) { warn("students.csv", row); continue; }
            try {
                int    roll = Integer.parseInt(row[0].trim());
                String name = row[1].trim();
                Student s = new Student(roll, name, 0);

                if (row.length > 2 && !row[2].trim().isEmpty()) {
                    for (String cid : row[2].trim().split("\\|"))
                        s.enrol(cid.trim());
                }
                list.add(s);
            } catch (NumberFormatException e) {
                System.out.println("  [CSVLoader] Skipping bad students row: " + Arrays.toString(row));
            }
        }
        return list;
    }

    /**
     * Loads results and attaches them to matching students in the provided list.
     * Returns the number of results successfully attached.
     */
    public static int loadResults(String filePath, List<Student> students) throws IOException {
        List<String[]> rows = readCSV(filePath);
        Map<Integer, Student> studentMap = new HashMap<>();
        for (Student s : students) studentMap.put(s.getRollNumber(), s);

        int attached = 0;
        for (String[] row : rows) {
            if (row.length < 4) { warn("results.csv", row); continue; }
            try {
                int    roll      = Integer.parseInt(row[0].trim());
                String aid       = row[1].trim();
                int    obtained  = Integer.parseInt(row[2].trim());
                int    maxMarks  = Integer.parseInt(row[3].trim());

                Student s = studentMap.get(roll);
                if (s == null) {
                    System.out.println("  [CSVLoader] No student with roll " + roll + " – skipping result row.");
                    continue;
                }
                s.addResult(new Result(roll, aid, obtained, maxMarks));
                attached++;
            } catch (NumberFormatException e) {
                System.out.println("  [CSVLoader] Skipping bad results row: " + Arrays.toString(row));
            }
        }
        return attached;
    }

    public static List<ActivitySelector_Schedule.StudySession> loadSessions(String filePath)
            throws IOException {
        List<String[]> rows = readCSV(filePath);
        List<ActivitySelector_Schedule.StudySession> list = new ArrayList<>();

        for (String[] row : rows) {
            if (row.length < 3) { warn("sessions.csv", row); continue; }
            try {
                String name  = row[0].trim();
                int    start = Integer.parseInt(row[1].trim());
                int    end   = Integer.parseInt(row[2].trim());
                if (start >= end) {
                    System.out.println("  [CSVLoader] Invalid session (start>=end): " + name);
                    continue;
                }
                list.add(new ActivitySelector_Schedule.StudySession(name, start, end));
            } catch (NumberFormatException e) {
                System.out.println("  [CSVLoader] Skipping bad sessions row: " + Arrays.toString(row));
            }
        }
        return list;
    }

    // ── Core CSV reader ──────────────────────────────────────────

    /**
     * Reads a CSV file, skips the header line and blank lines,
     * returns each data row as a String[].
     */
    private static List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (firstLine) { firstLine = false; continue; }  // skip header
                rows.add(line.split(",", -1));
            }
        }
        return rows;
    }

    private static void warn(String file, String[] row) {
        System.out.println("  [CSVLoader] Skipping incomplete row in " + file + ": " + Arrays.toString(row));
    }
}
