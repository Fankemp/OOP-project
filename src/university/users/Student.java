package university.users;

import java.io.Serial;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import university.exceptions.*;
import university.academic.*;
import university.enums.CourseType;

/**
 * Класс, представляющий студента университета.
 * Отрефакторен согласно принципам DRY и Low Coupling.
 */
public class Student extends User {
    @Serial
    private static final long serialVersionUID = 2026L;
    private static final Logger STUDENT_LOGGER = Logger.getLogger(Student.class.getName());

    private static final int MAX_CREDITS_LIMIT = 21;

    private double gpa;
    private String major;
    private int yearOfStudy;
    private int totalCredits;

    private final List<Course> courses;
    private final Map<Course, Integer> failCount;
    private final Map<Course, Mark> marks;

    public Student(String id, String firstName, String lastName, String email, String login, String password, String major, int yearOfStudy) {
        super(id, firstName, lastName, email, login, password);
        this.major = major;
        this.yearOfStudy = yearOfStudy;
        this.gpa = 0.0;
        this.totalCredits = 0;
        this.courses = new ArrayList<>();
        this.failCount = new HashMap<>();
        this.marks = new HashMap<>();
    }

    public void registerCourse(Course course) throws MaxCreditsException, CourseFailLimitException {
        CourseType effectiveType = course.getType();
        if ("SITE".equalsIgnoreCase(this.major) && course.getTargetMajor() != null
                && course.getTargetMajor().equalsIgnoreCase("OilGas")) {
            effectiveType = CourseType.FREE_ELECTIVE;
            System.out.println("[Info] Курс '" + course.getName() +
                    "' засчитан как FREE_ELECTIVE для студента SITE.");
        }

        if (this.totalCredits + course.getCredits() > MAX_CREDITS_LIMIT) {
            STUDENT_LOGGER.log(Level.WARNING, "MaxCreditsException for student {0}: Attempted {1} credits",
                    new Object[]{getId(), (totalCredits + course.getCredits())});
            throw new MaxCreditsException(this.totalCredits, course.getCredits());
        }

        if (hasExceededFailLimit(course)) {
            throw new CourseFailLimitException(this.getFullName(), course.getName());
        }

        if (!courses.contains(course)) {
            courses.add(course);
            totalCredits += course.getCredits();
            course.enrollStudent(this);
            STUDENT_LOGGER.log(Level.INFO, "Course {0} registered for student {1}", new Object[]{course.getName(), getId()});
        }
    }


    public void receiveMark(Course course, Mark mark) {
        if (!courses.contains(course)) {
            STUDENT_LOGGER.log(Level.WARNING, "Student {0} is not registered for course {1}", new Object[]{getId(), course.getName()});
            return;
        }

        marks.put(course, mark);
        STUDENT_LOGGER.log(Level.INFO, "Student {0} received mark {1} for course {2}",
                new Object[]{getId(), mark.getTotal(), course.getName()});

        if (mark.isFailed()) {
            incrementFailCount(course);
        }

        recalculateGpa();
    }

    private boolean hasExceededFailLimit(Course course) {
        return failCount.containsKey(course) && failCount.get(course) >= 3;
    }

    private void incrementFailCount(Course course) {
        int currentFails = failCount.getOrDefault(course, 0);
        failCount.put(course, currentFails + 1);
        STUDENT_LOGGER.log(Level.INFO, "Course {0} fail count increased to {1} for student {2}",
                new Object[]{course.getName(), failCount.get(course), getId()});
    }

    private void recalculateGpa() {
        if (marks.isEmpty()) {
            this.gpa = 0.0;
            return;
        }

        double totalPoints = 0.0;
        int gradedCredits = 0;

        for (Map.Entry<Course, Mark> entry : marks.entrySet()) {
            Course course = entry.getKey();
            Mark mark = entry.getValue();

            totalPoints += mark.getGpaPoints() * course.getCredits();
            gradedCredits += course.getCredits();
        }

        this.gpa = gradedCredits > 0 ? (totalPoints / gradedCredits) : 0.0;
    }

    /**
     * Выводит текущие оценки студента в консоль.
     */
    public void viewMarks() {
        if (marks.isEmpty()) {
            System.out.println("No marks available yet.");
            return;
        }
        // ИСПРАВЛЕНО (DRY): Переиспользовали метод mark.getLetterGrade() и mark.getTotal()
        marks.forEach((course, mark) ->
                System.out.printf("%s: %.1f (%s)%n", course.getName(), mark.getTotal(), mark.getLetterGrade()));
    }

    public Transcript getTranscript() {
        return new Transcript(this.marks);
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

    // --- Геттеры и Сеттеры (С защитой от внешней модификации коллекций) ---
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    
    List<Course> getCoursesInternal() { return courses; }
    public int getTotalCredits() { return totalCredits; }

    public Map<Course, Mark> getMarks() { return Collections.unmodifiableMap(marks); }
    public Map<Course, Integer> getFailCount() { return Collections.unmodifiableMap(failCount); }

    // --- Системные контракты равенства объектов ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return Objects.equals(getId(), student.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s', major='%s', year=%d, gpa=%.2f, credits=%d}",
                getId(), getFullName(), major, yearOfStudy, gpa, totalCredits);
    }
}