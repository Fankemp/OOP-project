package university.academic;

import university.users.Student;
import java.io.Serializable;
import java.io.Serial;
import java.util.Objects;

public class Mark implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;

    private double att1 = 0.0;
    private double att2 = 0.0;
    private double finalExam = 0.0;

    public Course course;
    public Student student;

    public Mark(Course course, Student student) {
        this.course = Objects.requireNonNull(course);
        this.student = Objects.requireNonNull(student);
    }

    public void setAtt1(double score) {
        if (score < 0 || score > 30) throw new IllegalArgumentException("First attestation must be 0-30");
        this.att1 = score;
    }

    public void setAtt2(double score) {
        if (score < 0 || score > 30) throw new IllegalArgumentException("Second attestation must be 0-30");
        this.att2 = score;
    }

    public void setFinalExam(double score) {
        if (score < 0 || score > 40) throw new IllegalArgumentException("Final exam must be 0-40");
        this.finalExam = score;
    }

    public double getAtt1() { return att1; }
    public double getAtt2() { return att2; }
    public double getFinalExam() { return finalExam; }

    public double getTotal() {
        return att1 + att2 + finalExam;
    }

    public boolean isFailed() {
        return getTotal() < 50.0;
    }

    public String getLetterGrade() {
        double total = getTotal();
        if (total >= 95) return "A";
        if (total >= 90) return "A-";
        if (total >= 85) return "B+";
        if (total >= 80) return "B";
        if (total >= 75) return "B-";
        if (total >= 70) return "C+";
        if (total >= 65) return "C";
        if (total >= 60) return "C-";
        if (total >= 55) return "D+";
        if (total >= 50) return "D";
        return "F";
    }

    public double getGpaPoints() {
        double total = getTotal();
        if (total >= 90) return 4.0;
        if (total >= 80) return 3.0;
        if (total >= 70) return 2.0;
        if (total >= 50) return 1.0;
        return 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mark mark = (Mark) o;
        return Double.compare(mark.att1, att1) == 0 &&
                Double.compare(mark.att2, att2) == 0 &&
                Double.compare(mark.finalExam, finalExam) == 0 &&
                course.equals(mark.course) &&
                student.equals(mark.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(att1, att2, finalExam, course, student);
    }

    @Override
    public String toString() {
        return String.format("Total: %.1f (%s)", getTotal(), getLetterGrade());
    }
}