package university.user;

import university.enums.*;
import university.academic.*;
import university.research.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Teacher extends Employee implements Researcher {
    private static final Logger TEACHER_LOGGER = Logger.getLogger(Teacher.class.getName());

    private TeacherPosition position;
    private List<Course> courses;
    private double rating;

    private int numberOfRatings;
    private double totalRatingSum;

    private List<ResearchPaper> papers;
    private List<ResearchProject> researchProjects;

    public Teacher(String id, String firstName, String lastName, String email, String login, String password,
                   double salary, String department, TeacherPosition position) {
        super(id, firstName, lastName, email, login, password, salary, department);
        this.position = position;
        this.courses = new ArrayList<>();
        this.papers = new ArrayList<>();
        this.researchProjects = new ArrayList<>();
        this.rating = 0.0;
        this.numberOfRatings = 0;
        this.totalRatingSum = 0.0;
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

    @Override
    public int calculateHIndex() {
        if (papers == null || papers.isEmpty()) {
            return 0;
        }

        int[] citations = new int[papers.size()];
        for (int i = 0; i < papers.size(); i++) {
            citations[i] = papers.get(i).getCitations();
        }

        Arrays.sort(citations);

        int hIndex = 0;
        int n = citations.length;
        for (int i = 0; i < n; i++) {
            int currentH = n - i;
            if (citations[i] >= currentH) {
                hIndex = currentH;
                break;
            }
        }
        return hIndex;
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> c) {
        List<ResearchPaper> sortedPapers = new ArrayList<>(papers);
        sortedPapers.sort(c);
        sortedPapers.forEach(System.out::println);
    }

    @Override
    public List<ResearchPaper> getResearchPapers() {
        return new ArrayList<>(papers);
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        return Collections.unmodifiableList(researchProjects);
    }

    public void addResearchPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) {
            papers.add(paper);
        }
    }

    public void addResearchProject(ResearchProject project) {
        if (project != null && !researchProjects.contains(project)) {
            researchProjects.add(project);
        }
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
        return String.format("Teacher{id='%s', name='%s', position=%s, rating=%.2f, totalPapers=%d, hIndex=%d}",
                getId(), getFullName(), position, rating, papers.size(), calculateHIndex());
    }
}