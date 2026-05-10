package university.academic;

import university.user.Student;
import university.user.Teacher;
import university.enums.CourseType;
import university.enums.LessonType;
import university.exceptions.MaxCreditsException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Course {
    private static final Logger COURSE_LOGGER = Logger.getLogger(Course.class.getName());

    private String code;
    private String name;
    private int credits;
    private CourseType type;
    private Teacher lectureTeacher;
    private Teacher practiceTeacher;
    private List<Student> students;

    public Course(String code, String name, int credits, CourseType type) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.type = type;
        this.students = new ArrayList<>();
    }

    public void addTeacher(Teacher teacher, LessonType lessonType) {
        if (lessonType == LessonType.LECTURE) {
            this.lectureTeacher = teacher;
            COURSE_LOGGER.log(Level.INFO, "Lecture teacher {0} added to course {1}",
                    new Object[]{teacher.getLastName(), name});
        } else if (lessonType == LessonType.PRACTICE) {
            this.practiceTeacher = teacher;
            COURSE_LOGGER.log(Level.INFO, "Practice teacher {0} added to course {1}",
                    new Object[]{teacher.getLastName(), name});
        }
    }

    public void enrollStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            COURSE_LOGGER.log(Level.INFO, "Student {0} enrolled in {1}",
                    new Object[]{student.getId(), name});
        }
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public CourseType getType() { return type; }
    public List<Student> getStudents() { return Collections.unmodifiableList(students); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(code, course.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Course{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", type=" + type +
                ", lecture=" + (lectureTeacher != null ? lectureTeacher.getLastName() : "TBA") +
                ", practice=" + (practiceTeacher != null ? practiceTeacher.getLastName() : "TBA") +
                '}';
    }
}