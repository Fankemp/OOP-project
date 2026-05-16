package university.storage;

import university.academic.Mark;
import university.exceptions.MaxCreditsException;
import university.users.User;
import university.academic.Course;
import university.communications.News;
import university.communications.Request;

import java.io.*;
import java.util.*;

/**
 * Глобальный контейнер данных Университета (Singleton + Memento).
 * Хранит, сериализует и предоставляет доступ к корневым коллекциям системы.
 */
public class University implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;

    private static final String DATA_FILE = "university_data.ser";
    private static final University INSTANCE = new University();

    // Четыре утвержденные базовые коллекции системы
    private final List<User> users = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();
    private final List<News> news = new ArrayList<>();
    private final List<Request> requests = new ArrayList<>();

    // Приватный конструктор исключает создание через new извне
    private University() {}

    public static University getInstance() {
        return INSTANCE;
    }

    public Optional<User> authenticate(String login, String password) {
        if (login == null || password == null) {
            return Optional.empty();
        }
        return users.stream()
                .filter(user -> user.verifyCredentials(login, password))
                .findFirst();
    }

    public synchronized void enrollStudentInCourse(university.users.Student student, Course course) throws MaxCreditsException {
        Objects.requireNonNull(student, "Студент не может быть null");
        Objects.requireNonNull(course, "Курс не может быть null");

        // 1. Проверяем лимиты внутри самого студента (выбросит исключение, если лимит превышен)
        student.registerCourse(course);

        // 2. Если исключение не вылетело, добавляем студента в ведомость курса
        course.enrollStudent(student);

        // 3. Автоматически создаем пустую структуру оценок для этого курса у студента
        student.getMarks().put(course, new Mark(course, student));
    }

    public synchronized void save() {
        UniversitySnapshot snapshot = new UniversitySnapshot(users, courses, news, requests);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(snapshot);
        } catch (IOException e) {
            throw new RuntimeException("Критическая ошибка при сохранении данных на диск", e);
        }
    }

    public synchronized void load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            UniversitySnapshot snapshot = (UniversitySnapshot) ois.readObject();

            this.users.clear();
            this.courses.clear();
            this.news.clear();
            this.requests.clear();

            this.users.addAll(snapshot.users());
            this.courses.addAll(snapshot.courses());
            this.news.addAll(snapshot.news());
            this.requests.addAll(snapshot.requests());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Критическая ошибка при чтении базы данных", e);
        }
    }


    public synchronized void addUser(User user) {
        this.users.add(Objects.requireNonNull(user, "Пользователь не может быть null"));
    }

    public synchronized void addCourse(Course course) {
        this.courses.add(Objects.requireNonNull(course, "Курс не может быть null"));
    }

    public synchronized void addNews(News newsItem) {
        this.news.add(Objects.requireNonNull(newsItem, "Новость не может быть null"));
    }

    public synchronized void addRequest(Request request) {
        this.requests.add(Objects.requireNonNull(request, "Запрос не может быть null"));
    }

    // Защита от прямого изменения списков из других пакетов
    public List<User> getUsers() { return Collections.unmodifiableList(users); }
    public List<Course> getCourses() { return Collections.unmodifiableList(courses); }
    public List<News> getNews() { return Collections.unmodifiableList(news); }
    public List<Request> getRequests() { return Collections.unmodifiableList(requests); }

    private record UniversitySnapshot(
            List<User> users,
            List<Course> courses,
            List<News> news,
            List<Request> requests
    ) implements Serializable {
        @Serial private static final long serialVersionUID = 2026L;
    }

    public List<university.users.Student> getStudentsSortedByGpa() {
        return users.stream()
                .filter(user -> user instanceof university.users.Student) // Фильтруем только студентов
                .map(user -> (university.users.Student) user)
                // Сортируем по GPA в обратном порядке (от большего к меньшему)
                .sorted((s1, s2) -> Double.compare(s2.getTranscript().calculateGpa(), s1.getTranscript().calculateGpa()))
                .collect(java.util.stream.Collectors.toList());
    }
}