package university.user;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import university.exceptions.*;
import university.academic.*;

public class Student extends User {
    private static final Logger STUDENT_LOGGER = Logger.getLogger(Student.class.getName());

    private double gpa;
    private String major;
    private int yearOfStudy;
    private List<Course> courses;
    private int totalCredits;
    private Map<Course, Integer> failCount;
    private Map<Course, Mark> marks;

    public Student(String id, String firstName, String lastName, String email, String login, String password, String major, int yearOfStudy) {
        super(id, firstName, lastName, email, login, password);
        this.major = major;
        this.yearOfStudy = yearOfStudy;
        this.gpa = 0;
        this.totalCredits = 0;
        this.courses = new ArrayList<>();
        this.failCount = new HashMap<>();
        this.marks = new HashMap<>();
    }

    public void registerCourse(Course course) throws MaxCreditsException, CourseFailLimitException {
        if (this.totalCredits + course.getCredits() > 21) {
            STUDENT_LOGGER.log(Level.WARNING, "MaxCreditsException for student {0}: Attempted {1} credits",
                    new Object[]{getId(), (totalCredits + course.getCredits())});
            throw new MaxCreditsException("Exceeded credits limit (max 21). Current credits: " + totalCredits);
        }

        if (failCount.containsKey(course) && failCount.get(course) >= 3) {
            STUDENT_LOGGER.log(Level.SEVERE, "CourseFailLimitException: Student {0} failed {1} too many times",
                    new Object[]{getId(), course.getName()});
            throw new CourseFailLimitException("You reached retakes limit for course: " + course.getName());
        }

        if (!courses.contains(course)) {
            courses.add(course);
            totalCredits += course.getCredits();
            STUDENT_LOGGER.log(Level.INFO, "Course {0} registered successfully for student {1}", new Object[]{course.getName(), getId()});
        }
    }

    public void viewMarks() {
        System.out.println("Marks for student " + getFirstName() + ":");
        marks.forEach((course, mark) ->
                System.out.println(course.getName() + ": " + mark.getNumericalValue()));
    }

    public Transcript getTranscript() {
        // Это на потом
        return new Transcript(this.marks, this.gpa);
    }

    public void rateTeacher(Teacher teacher, int rating) {
        if (rating < 1 || rating > 10) {
            System.out.println("Rating should be between 1 and 10");
            return;
        }
        teacher.addRating(rating);
        STUDENT_LOGGER.log(Level.INFO, "Student {0} rated teacher {1} as {2}",
                new Object[]{getId(), teacher.getLastName(), rating});
    }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public List<Course> getCourses() { return Collections.unmodifiableList(courses); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), major, yearOfStudy);
    }

    @Override
    public String toString() {
        return "Student{" +
                "major='" + major + '\'' +
                ", gpa=" + gpa +
                ", year=" + yearOfStudy +
                "} " + super.toString();
    }
}
