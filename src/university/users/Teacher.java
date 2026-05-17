package university.users;

import university.enums.*;
import university.academic.*;
import university.research.*;
import java.io.Serial;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс Преподавателя университета.
 * ИСПРАВЛЕНО: Научная деятельность полностью делегирована в ResearchProfile через default-методы.
 */
public class Teacher extends Employee implements Researcher {
    @Serial
    private static final long serialVersionUID = 2026L;
    private static final Logger TEACHER_LOGGER = Logger.getLogger(Teacher.class.getName());

    private TeacherPosition position;
    private List<Course> courses;
    private double rating;

    private int numberOfRatings;
    private double totalRatingSum;

    // ИСПРАВЛЕНО: Вместо кучи списков статей и проектов внедряем один чистый профиль-делегат
    private final ResearchProfile researchProfile;

    public Teacher(String id, String firstName, String lastName, String email, String login, String password,
                   double salary, String department, TeacherPosition position) {
        super(id, firstName, lastName, email, login, password, salary, department);
        this.position = position;
        this.courses = new ArrayList<>();
        this.rating = 0.0;
        this.numberOfRatings = 0;
        this.totalRatingSum = 0.0;

        this.researchProfile = new ResearchProfile(id);
    }

    @Override
    public ResearchProfile getResearchProfile() {
        return this.researchProfile;
    }

    public void putMark(Student student, Course course, Mark mark) {
        if (courses.contains(course)) {
            student.receiveMark(course, mark);
            TEACHER_LOGGER.log(Level.INFO, "Teacher {0} assigned mark to student {1}", new Object[]{getLastName(), student.getId()});
        } else {
            TEACHER_LOGGER.log(Level.WARNING, "Teacher {0} cannot assign mark: they do not teach course {1}", new Object[]{getLastName(), course.getName()});
        }
    }

    public void sendComplaint(Student student, Manager dean, UrgencyLevel level) {
        System.out.printf("Complaint regarding student %s sent to Dean %s with urgency level %s%n",
                student.getFullName(), dean.getFullName(), level);
    }

    public void viewStudents() {
        System.out.println("Students in your courses:");
        for (Course c : courses) {
            System.out.println("Course: " + c.getName());
            c.getStudents().forEach(s -> System.out.println(" - " + s.getFullName()));
        }
    }

    public void manageCourse(Course course) {
        addCourse(course);
    }

    public void viewCourses() {
        courses.forEach(c -> System.out.println(c.getCode() + " : " + c.getName()));
    }

    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
            TEACHER_LOGGER.log(Level.INFO, "Course {0} added to Teacher {1}", new Object[]{course.getName(), getId()});
        }
    }

    public void addRating(int newRating) {
        if (newRating < 1 || newRating > 10) {
            System.out.println("Rating must be between 1 and 10.");
            return;
        }
        this.numberOfRatings++;
        this.totalRatingSum += newRating;
        this.rating = this.totalRatingSum / this.numberOfRatings;

        TEACHER_LOGGER.log(Level.INFO, "Teacher {0} received new rating: {1}. Updated average rating: {2}",
                new Object[]{getId(), newRating, this.rating});
    }

    public TeacherPosition getPosition() { return position; }
    public void setPosition(TeacherPosition position) { this.position = position; }

    public double getRating() { return rating; }
    public int getNumberOfRatings() { return numberOfRatings; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teacher)) return false;
        if (!super.equals(o)) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(getId(), teacher.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }

    @Override
    public String toString() {
        // ИСПРАВЛЕНО: Методы getPapers() и calculateHIndex() вызываются напрямую из интерфейса Researcher
        return String.format("Teacher{id='%s', name='%s', position=%s, rating=%.2f, totalPapers=%d, hIndex=%d}",
                getId(), getFullName(), position, rating, getPapers().size(), calculateHIndex());
    }
}