package core;

public class Result implements Comparable<Result> {

    private final int rollNumber;
    private final String assessmentId;
    private final int marksObtained;
    private final int maxMarks;

    public Result(int rollNumber, String assessmentId, int marksObtained, int maxMarks) {
        this.rollNumber = rollNumber;
        this.assessmentId = assessmentId;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
    }

    public double getPercentage() {
        return (maxMarks == 0) ? 0 : (marksObtained * 100.0 / maxMarks);
    }

    @Override
    public int compareTo(Result other) {
        return Double.compare(this.getPercentage(), other.getPercentage());
    }

    public int getRollNumber()     { return rollNumber; }
    public String getAssessmentId(){ return assessmentId; }
    public int getMarksObtained()  { return marksObtained; }
    public int getMaxMarks()       { return maxMarks; }

    @Override
    public String toString() {
        return String.format("Result[roll=%d, exam=%s, score=%d/%d (%.1f%%)]",
                rollNumber, assessmentId, marksObtained, maxMarks, getPercentage());
    }
}
