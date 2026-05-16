package university.users;

import university.enums.*;
import university.academic.*;
import university.research.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Teacher extends Employee {
    private static final Logger TEACHER_LOGGER = Logger.getLogger(Teacher.class.getName());

    private TeacherPosition position;
    private List<Course> courses;
    private double rating;
    private List<ResearchPaper> papers;
    private List<ResearchProject> researchProjects;

    public Teacher(String id, String firstName, String lastName, String email, String login, String password,
                   double salary, TeacherPosition position) {
        super(id, firstName, lastName, email, login, password, salary);
        this.position = position;
        this.courses = new ArrayList<>();
        this.papers = new ArrayList<>();
        this.researchProjects = new ArrayList<>();
        this.rating = 0.0;
    }

    public void putMark(Student student, Course course, Mark mark) {
        if (courses.contains(course)) {
            student.getMarks().put(course, mark);
            TEACHER_LOGGER.log(Level.INFO, "Teacher {0} assigned mark to student {1}", new Object[]{getLastName(), student.getId()});
        }
    }

    public void sendComplaint(Student student, Manager dean, UrgencyLevel level) {
        String complaintBody = "Complaint against student " + student.getFirstName() + " " + student.getLastName();
        // Это закончим потом с жалобами
        TEACHER_LOGGER.log(Level.WARNING, "Complaint sent to Dean {0} with urgency {1}", new Object[]{dean.getLastName(), level});
    }

    public void viewStudents() {
        System.out.println("Students in courses taught by " + getLastName() + ":");
        for (Course course : courses) {
            System.out.println("Course: " + course.getName());
            course.getStudents().forEach(s -> System.out.println(" - " + s.getFirstName() + " " + s.getLastName()));
        }
    }

    public void manageCourse(Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
            TEACHER_LOGGER.log(Level.INFO, "Teacher {0} started managing course {1}", new Object[]{getLastName(), course.getName()});
        }
    }

    public void viewCourses() {
        courses.forEach(c -> System.out.println(c.getCode() + ": " + c.getName()));
    }

    public int calculateHIndex() {
        // Это на потом
        // Пока просто возвращаем заглушку на основе количества статей
        return papers.size();
    }

    public void printPapers(Comparator<ResearchPaper> c) {
        List<ResearchPaper> sortedPapers = new ArrayList<>(papers);
        sortedPapers.sort(c);
        sortedPapers.forEach(System.out::println);
    }

    public List<ResearchProject> getResearchProjects() {
        return Collections.unmodifiableList(researchProjects);
    }

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
        return Objects.hash(super.hashCode(), position);
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "position=" + position +
                ", rating=" + rating +
                ", salary=" + getSalary() +
                "} " + super.toString();
    }

    public TeacherPosition getPosition() { return position; }
    public void setPosition(TeacherPosition position) { this.position = position; }
    public double getRating() { return rating; }
    public void addRating(int newRating) {
        this.rating = (this.rating + newRating) / 2;
    }
}