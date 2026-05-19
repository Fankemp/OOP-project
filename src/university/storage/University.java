package university.storage;


import university.exceptions.CourseFailLimitException;
import university.exceptions.MaxCreditsException;
import university.users.User;
import university.academic.Course;
import university.communications.News;
import university.communications.Request;
import university.users.StudentOrganization;
import university.communications.OfficialMessage;

import java.io.*;
import java.util.*;


public class University implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;
    private List<university.communications.Message> allMessages = new java.util.ArrayList<>();
    private static final String DATA_FILE = "university_data.ser";
    private static final University INSTANCE = new University();

    private final List<User> users = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();
    private final List<News> news = new ArrayList<>();
    private final List<Request> requests = new ArrayList<>();
    private final List<StudentOrganization> organizations = new ArrayList<>();
    private final List<university.research.journal.UniversityJournal> journals = new ArrayList<>();
    private final List<OfficialMessage> officialMessages = new ArrayList<>();
    
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

    public synchronized void enrollStudentInCourse(university.users.Student student, Course course) throws MaxCreditsException, CourseFailLimitException {
        Objects.requireNonNull(student, "Студент не может быть null");
        Objects.requireNonNull(course, "Курс не может быть null");

        student.registerCourse(course);

        course.enrollStudent(student);


    }

    public synchronized void save() {
    	UniversitySnapshot snapshot = new UniversitySnapshot(users, courses, news, requests, allMessages);
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
            if (snapshot.messages() != null) this.allMessages.addAll(snapshot.messages());
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
    
    public synchronized void addOrganization(StudentOrganization org) {
        organizations.add(Objects.requireNonNull(org));
    }

    public List<StudentOrganization> getOrganizations() {
        return Collections.unmodifiableList(organizations);
    }
    
    public synchronized void addJournal(university.research.journal.UniversityJournal journal) {
        journals.add(Objects.requireNonNull(journal));
    }

    public List<university.research.journal.UniversityJournal> getJournals() {
        return Collections.unmodifiableList(journals);
    }
    
    public synchronized void addOfficialMessage(OfficialMessage msg) {
        officialMessages.add(Objects.requireNonNull(msg));
    }

    public List<OfficialMessage> getOfficialMessages() {
        return Collections.unmodifiableList(officialMessages);
    }
    
    public synchronized boolean removeUser(String userId) {
        return users.removeIf(u -> u.getId().equals(userId));
    }

    public User findUserById(String userId) {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst().orElse(null);
    }

    public void sendMessage(university.users.User sender, university.users.User receiver, String text) {
        if (sender == null || receiver == null || text == null || text.trim().isEmpty()) {
            return;
        }
        university.communications.Message msg = new university.communications.Message(sender, receiver, text);
        this.allMessages.add(msg);
    }

    public java.util.List<university.communications.Message> getMessagesForUser(university.users.User user) {
        if (user == null) return java.util.Collections.emptyList();

        // фильтруем все сообщения, где текущий пользователь является получателем
        return this.allMessages.stream()
                .filter(m -> m.getReceiver().equals(user))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<User> getUsers() { return Collections.unmodifiableList(users); }
    public List<Course> getCourses() { return Collections.unmodifiableList(courses); }
    public List<News> getNews() { return Collections.unmodifiableList(news); }
    public List<Request> getRequests() { return Collections.unmodifiableList(requests); }

    private record UniversitySnapshot(
            List<User> users,
            List<Course> courses,
            List<News> news,
            List<Request> requests,
            List<university.communications.Message> messages
    ) implements Serializable {
        @Serial private static final long serialVersionUID = 2026L;
    }

    public List<university.users.Student> getStudentsSortedByGpa() {
        return users.stream()
                .filter(user -> user instanceof university.users.Student) 
                .map(user -> (university.users.Student) user)
                // сортируем по GPA в обратном порядке 
                .sorted((s1, s2) -> Double.compare(s2.getTranscript().calculateGpa(), s1.getTranscript().calculateGpa()))
                .collect(java.util.stream.Collectors.toList());
    }
}