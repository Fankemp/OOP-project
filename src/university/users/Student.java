package university.users; // или university.user в зависимости от названия вашего пакета

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import university.exceptions.*;
import university.academic.*;
import university.users.User; // Импорт базового класса User
import university.users.Teacher; // Импорт класса Teacher

public class Student extends User {
    private static final Logger STUDENT_LOGGER = Logger.getLogger(Student.class.getName());

    private double gpa;
    private String major;
    private int yearOfStudy;
    private final List<Course> courses;
    private int totalCredits;
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
        Objects.requireNonNull(course, "Курс не может быть null");

        if (this.totalCredits + course.getCredits() > 21) {
            STUDENT_LOGGER.log(Level.WARNING, "MaxCreditsException для студента {0}: Попытка взять {1} кредитов",
                    new Object[]{getId(), (totalCredits + course.getCredits())});
            throw new MaxCreditsException("Превышен лимит кредитов (макс 21). Текущие кредиты: " + totalCredits);
        }

        if (failCount.containsKey(course) && failCount.get(course) >= 3) {
            STUDENT_LOGGER.log(Level.SEVERE, "CourseFailLimitException: Студент {0} превысил лимит фейлов по курсу {1}",
                    new Object[]{getId(), course.getName()});
            throw new CourseFailLimitException("Достигнут лимит пересдач (3) для курса: " + course.getName());
        }

        if (!courses.contains(course)) {
            courses.add(course);
            totalCredits += course.getCredits();
            STUDENT_LOGGER.log(Level.INFO, "Курс {0} успешно зарегистрирован для студента {1}", new Object[]{course.getName(), getId()});
        }
    }

    public void viewMarks() {
        System.out.println("Оценки студента " + getFirstName() + ":");
        if (marks.isEmpty()) {
            System.out.println("Нет зарегистрированных оценок.");
            return;
        }
        marks.forEach((course, mark) ->
                System.out.println(course.getName() + ": " + mark.getTotal() + " [" + mark.getLetterGrade() + "]"));
    }

    public Transcript getTranscript() {
        Transcript transcript = new Transcript();
        // Наполняем транскрипт данными из текущей мапы оценок студента
        this.marks.forEach(transcript::addRecord);
        // Обновляем внутреннее поле gpa актуальным значением, рассчитанным транскриптом
        this.gpa = transcript.calculateGpa();
        return transcript;
    }

    public void rateTeacher(Teacher teacher, int rating) {
        if (rating < 1 || rating > 10) {
            System.out.println("Рейтинг должен быть от 1 до 10");
            return;
        }
        teacher.addRating(rating);
        STUDENT_LOGGER.log(Level.INFO, "Студент {0} поставил преподавателю {1} оценку {2}",
                new Object[]{getId(), teacher.getLastName(), rating});
    }


    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public List<Course> getCourses() { return Collections.unmodifiableList(courses); }

    // ВАЖНО ДЛЯ ТВОЕГО СИНГЛТОНА: Предоставляем доступ к мапам для управления из University
    public Map<Course, Mark> getMarks() { return marks; }
    public Map<Course, Integer> getFailCount() { return failCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return Objects.equals(this.getId(), student.getId());
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
                ", totalCredits=" + totalCredits +
                "} " + super.toString();
    }
}