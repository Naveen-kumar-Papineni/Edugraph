package greedy;

import java.util.*;

// Activity Selection: pick max non-overlapping study sessions (greedy)
public class ActivitySelector_Schedule {

    public static class StudySession {
        public final String name;
        public final int start;
        public final int end;

        public StudySession(String name, int start, int end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return String.format("%s(%02d:00-%02d:00)", name, start, end);
        }
    }

    // Greedy: sort by end time, always pick earliest-finishing session
    public List<StudySession> selectOptimalSessions(List<StudySession> sessions) {
        List<StudySession> sorted = new ArrayList<>(sessions);
        sorted.sort(Comparator.comparingInt(s -> s.end));

        List<StudySession> selected = new ArrayList<>();
        int lastEnd = -1;

        for (StudySession session : sorted) {
            if (session.start >= lastEnd) {
                selected.add(session);
                lastEnd = session.end;
            }
        }
        return selected;
    }

    public void printSchedule(List<StudySession> sessions) {
        System.out.println("  [ActivitySelector] Optimal daily schedule:");
        for (StudySession s : sessions)
            System.out.println("    -> " + s);
    }
}
