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

    public Course(String code, String name, int credits, CourseType type) {
        this.code = Objects.requireNonNull(code, "Код курса не может быть null").toUpperCase();
        this.name = Objects.requireNonNull(name, "Название курса не может быть null");
        this.type = Objects.requireNonNull(type, "Тип курса не может быть null");
        if (credits <= 0) throw new IllegalArgumentException("Количество кредитов должно быть положительным");
        this.credits = credits;
    }


    public void addTeacher(Teacher teacher, LessonType lessonType) {
        Objects.requireNonNull(teacher, "Преподаватель не может быть null");
        if (lessonType == LessonType.LECTURE) {
            this.lectureTeacher = teacher;
        } else if (lessonType == LessonType.PRACTICE) {
            this.practiceTeacher = teacher;
        }
    }


    public void enrollStudent(Student student) {
        Objects.requireNonNull(student, "Студент не может быть null");
        if (!students.contains(student)) {
            this.students.add(student);
        }
    }

    public void addLesson(Lesson lesson) {
        this.lessons.add(Objects.requireNonNull(lesson, "Занятие не может быть null"));
    }


    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public CourseType getType() { return type; }
    public Teacher getLectureTeacher() { return lectureTeacher; }
    public Teacher getPracticeTeacher() { return practiceTeacher; }
    public List<Student> getStudents() { return Collections.unmodifiableList(students); }
    public List<Lesson> getLessons() { return Collections.unmodifiableList(lessons); }


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
        return String.format("[%s] %s (%d кредитов, %s)", code, name, credits, type);
    }
}