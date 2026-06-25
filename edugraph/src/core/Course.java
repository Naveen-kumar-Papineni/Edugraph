package core;

import java.util.ArrayList;
import java.util.List;

public class Course implements Comparable<Course> {

    private final String courseId;
    private final String name;
    private final int credits;
    private final int difficulty;
    private int popularity;
    private List<String> prerequisiteIds;

    public Course(String courseId, String name, int credits, int difficulty) {
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.difficulty = difficulty;
        this.popularity = 0;
        this.prerequisiteIds = new ArrayList<>();
    }

    public void addPrerequisite(String prereqId) {
        prerequisiteIds.add(prereqId);
    }

    public void incrementPopularity() { popularity++; }

    @Override
    public int compareTo(Course other) {
        return Integer.compare(other.popularity, this.popularity); // descending
    }

    public String getCourseId()        { return courseId; }
    public String getName()            { return name; }
    public int getCredits()            { return credits; }
    public int getDifficulty()         { return difficulty; }
    public int getPopularity()         { return popularity; }
    public List<String> getPrerequisiteIds() { return prerequisiteIds; }

    @Override
    public String toString() {
        return String.format("Course[id=%s, name=%s, credits=%d, diff=%d]",
                courseId, name, credits, difficulty);
    }
}
