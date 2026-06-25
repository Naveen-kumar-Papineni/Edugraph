package core;

public class Assessment {

    private final String assessmentId;
    private final String courseId;
    private final String title;
    private final int maxMarks;

    public Assessment(String assessmentId, String courseId, String title, int maxMarks) {
        this.assessmentId = assessmentId;
        this.courseId = courseId;
        this.title = title;
        this.maxMarks = maxMarks;
    }

    public String getAssessmentId() { return assessmentId; }
    public String getCourseId()     { return courseId; }
    public String getTitle()        { return title; }
    public int getMaxMarks()        { return maxMarks; }

    @Override
    public String toString() {
        return String.format("Assessment[id=%s, title=%s, max=%d]", assessmentId, title, maxMarks);
    }
}
