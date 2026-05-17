package university.academic;

import java.io.Serializable;
import java.io.Serial;
import java.util.*;

public class Transcript implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;
    private final Map<Course, Mark> records = new LinkedHashMap<>();
    public Transcript() {}

    public Transcript(Map<Course, Mark> initialRecords) {
        if (initialRecords != null) {
            this.records.putAll(initialRecords);
        }
    }

    public void addRecord(Course course, Mark mark) {
        Objects.requireNonNull(course, "Course cannot be null");
        Objects.requireNonNull(mark, "Mark cannot be null");
        this.records.put(course, mark);
    }

    public double calculateGpa() {
        if (records.isEmpty()) return 0.0;

        double totalWeightedPoints = records.entrySet().stream()
                .mapToDouble(entry -> entry.getValue().getGpaPoints() * entry.getKey().getCredits())
                .sum();

        int totalCredits = records.keySet().stream()
                .mapToInt(Course::getCredits)
                .sum();

        return totalCredits == 0 ? 0.0 : totalWeightedPoints / totalCredits;
    }

    public Map<Course, Mark> getRecords() {
        return Collections.unmodifiableMap(records);
    }

    @Override
    public String toString() {
        if (records.isEmpty()) return "Transcript is empty.";

        StringBuilder sb = new StringBuilder("========== ACADEMIC TRANSCRIPT ==========\n");
        records.forEach((course, mark) ->
                sb.append(String.format("%s: %-20s | Grade: %-5s | %s\n",
                        course.getCode(), course.getName(), mark.getLetterGrade(), mark.toString()))
        );
        sb.append("--------------------------------------------\n");
        sb.append(String.format("Total Cumulative GPA: %.2f\n", calculateGpa()));
        sb.append("============================================");
        return sb.toString();
    }
}