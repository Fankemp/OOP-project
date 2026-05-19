package university.academic;

import university.enums.CourseType;
import university.enums.LessonType;
import university.users.Student;
import university.users.Teacher;

import java.io.Serializable;
import java.io.Serial;
import java.util.*;

public class Course implements Serializable, Comparable<Course> {
    @Serial
    private static final long serialVersionUID = 2026L;

    private final String code;
    private final String name;
    private final int credits;
    private final CourseType type;
    private Teacher lectureTeacher;
    private Teacher practiceTeacher;
    private final List<Student> students = new ArrayList<>();
    private final List<Lesson> lessons = new ArrayList<>();

    
    private String targetMajor;
    private int targetYear;
    private boolean openForRegistration = false;

    public Course(String code, String name, int credits, CourseType type) {
        this.code = Objects.requireNonNull(code, "Course code cannot be null").toUpperCase();
        this.name = Objects.requireNonNull(name, "Course name cannot be null");
        this.type = Objects.requireNonNull(type, "Course type cannot be null");
        if (credits <= 0) throw new IllegalArgumentException("Credits count must be a positive integer");
        this.credits = credits;
    }

    public void addTeacher(Teacher teacher) {
        addTeacher(teacher, LessonType.LECTURE);
    }

    public void addTeacher(Teacher teacher, LessonType lessonType) {
        Objects.requireNonNull(teacher, "Teacher cannot be null");
        if (lessonType == LessonType.LECTURE) {
            this.lectureTeacher = teacher;
        } else if (lessonType == LessonType.PRACTICE) {
            this.practiceTeacher = teacher;
        }
    }

    public void enrollStudent(Student student) {
        Objects.requireNonNull(student, "Student cannot be null");
        if (!students.contains(student)) {
            this.students.add(student);
        }
    }

    public void addLesson(Lesson lesson) {
        this.lessons.add(Objects.requireNonNull(lesson, "Lesson cannot be null"));
    }

   
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public CourseType getType() { return type; }
    public Teacher getLectureTeacher() { return lectureTeacher; }
    public Teacher getPracticeTeacher() { return practiceTeacher; }
    public List<Student> getStudents() { return Collections.unmodifiableList(students); }
    public List<Lesson> getLessons() { return Collections.unmodifiableList(lessons); }

    
    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }

    public int getTargetYear() { return targetYear; }
    public void setTargetYear(int yearOfStudy) { this.targetYear = yearOfStudy; }

    public boolean isOpenForRegistration() { return openForRegistration; }
    public void setOpenForRegistration(boolean openForRegistration) { this.openForRegistration = openForRegistration; }

    @Override
    public int compareTo(Course o) {
        return this.code.compareTo(o.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return code.equalsIgnoreCase(course.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d credits, %s)", code, name, credits, type);
    }
}