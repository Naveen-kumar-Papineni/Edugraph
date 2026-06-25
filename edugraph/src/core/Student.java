package core;

import java.util.ArrayList;
import java.util.List;

public class Student implements Comparable<Student> {

    private final int rollNumber;
    private final String name;
    private double gpa;
    private List<String> enrolledCourses;
    private List<Result> results;

    public Student(int rollNumber, String name, double gpa) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.gpa = gpa;
        this.enrolledCourses = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    public void enrol(String courseId) {
        if (!enrolledCourses.contains(courseId))
            enrolledCourses.add(courseId);
    }

    public void addResult(Result r) {
        results.add(r);
        recomputeGPA();
    }

    private void recomputeGPA() {
        if (results.isEmpty()) return;
        double sum = 0;
        for (Result r : results)
            sum += r.getPercentage();
        this.gpa = (sum / results.size()) / 10.0;
    }

    @Override
    public int compareTo(Student other) {
        return Double.compare(other.gpa, this.gpa); // descending
    }

    public int getRollNumber()            { return rollNumber; }
    public String getName()               { return name; }
    public double getGpa()                { return gpa; }
    public List<String> getEnrolledCourses() { return enrolledCourses; }
    public List<Result> getResults()      { return results; }

    @Override
    public String toString() {
        return String.format("Student[roll=%d, name=%s, gpa=%.2f]", rollNumber, name, gpa);
    }
}
