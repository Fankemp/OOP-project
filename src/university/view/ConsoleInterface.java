package university.view;

import university.storage.University;
import university.academic.Course;
import university.enums.*;
import university.exceptions.MaxCreditsException;
import university.exceptions.CourseFailLimitException;
import university.storage.UserFactory;
import university.users.*;
import university.communications.Message;
import university.communications.Request;
import university.academic.Mark;
import university.enums.UrgencyLevel;
import java.util.List;
import university.research.Researcher;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import university.communications.News;
import university.exceptions.LowHIndexException;
import university.users.StudentOrganization;

import university.research.ResearchManager;
import university.enums.LessonType;
import university.communications.OfficialMessage;
import university.enums.DegreeType;
import university.users.GraduateStudent;

/**
 * Слой представления (View). Отвечает за весь интерактивный текстовый интерфейс системы KBTU.
 * Синхронизировано: логи, сообщения, заявки и управление академическими процессами.
 */
public class ConsoleInterface {
    private static final University university = University.getInstance();
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void start() {
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                System.out.println("\n========================================");
                System.out.println("       Университетская Система KBTU     ");
                System.out.println("========================================");
                System.out.println("1. Авторизация (Login)");
                System.out.println("2. Сохранить данные и выйти (Save & Exit)");
                System.out.print("Выберите действие: ");

                String choice = scanner.nextLine().trim();
                if (choice.equals("1")) {
                    showLoginMenu();
                } else if (choice.equals("2")) {
                    university.save();
                    System.out.println("База данных успешно сохранена на диск. Работа завершена.");
                    running = false;
                } else {
                    System.out.println("Ошибка: неверный пункт меню.");
                }
            } else {
                routeUserToMenu();
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n--- Экран Авторизации ---");
        System.out.print("Введите логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Введите пароль (123): ");
        String password = scanner.nextLine().trim();

        Optional<User> auth = university.authenticate(login, password);
        if (auth.isPresent()) {
            currentUser = auth.get();
            System.out.printf("Успешный вход! Ваша роль в системе: %s%n", currentUser.getClass().getSimpleName());
        } else {
            System.out.println("Ошибка: Неверный логин или пароль.");
        }
    }

    private static void routeUserToMenu() {
        if (currentUser instanceof Admin) {
            runAdminMenu((Admin) currentUser);
        } else if (currentUser instanceof Manager) {
            runManagerMenu((Manager) currentUser);
        } else if (currentUser instanceof Teacher) {
            runTeacherMenu((Teacher) currentUser);
        } else if (currentUser instanceof GraduateStudent) {
            runGradStudentMenu((GraduateStudent) currentUser);
        } else if (currentUser instanceof Student) {
            runStudentMenu((Student) currentUser);
        } else if (currentUser instanceof TechSupportSpecialist) {
            runTechSupportMenu((TechSupportSpecialist) currentUser);
        }
    }
    
    private static void runGradStudentMenu(GraduateStudent student) {
        System.out.println("\n--- [Graduate Student Panel] ---");
        System.out.println("1. Зарегистрироваться на курс");
        System.out.println("2. Посмотреть оценки");
        System.out.println("3. Транскрипт");
        System.out.println("4. Отправить сообщение");
        System.out.println("5. Посмотреть почтовый ящик");
        System.out.println("6. Оценить преподавателя");
        System.out.println("7. Research меню");
        System.out.println("8. Назначить научного руководителя");
        System.out.println("9. Logout");
        System.out.print("Choice: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.println("\n--- Доступные курсы ---");
                university.getCourses().forEach(System.out::println);
                System.out.print("Course code: ");
                String code = scanner.nextLine().trim();
                Course course = university.getCourses().stream()
                        .filter(c -> c.getCode().equalsIgnoreCase(code))
                        .findFirst().orElse(null);
                if (course == null) { System.out.println("Course not found."); return; }
                try {
                    student.registerCourse(course);
                    System.out.println("Registered successfully.");
                } catch (MaxCreditsException | CourseFailLimitException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case "2" -> student.viewMarks();
            case "3" -> System.out.println(student.getTranscript().toString());
            case "4" -> handleSendMessageMenu(student);
            case "5" -> handleViewMailboxMenu(student);
            case "6" -> handleRateTeacher(student);
            case "7" -> handleResearcherMenu(student);
            case "8" -> handleSetSupervisor(student);
            case "9" -> { currentUser = null; System.out.println("Logged out."); }
            default  -> System.out.println("Invalid choice.");
        }
    }

    private static void handleSetSupervisor(GraduateStudent student) {
        System.out.println("\n--- Set Research Supervisor ---");
        List<Teacher> teachers = university.getUsers().stream()
                .filter(u -> u instanceof Teacher)
                .map(u -> (Teacher) u)
                .toList();
        if (teachers.isEmpty()) { System.out.println("No teachers in system."); return; }
        teachers.forEach(t -> System.out.printf("  login: %-15s | %s | h-index: %d%n",
                t.getLogin(), t.getFullName(), t.calculateHIndex()));

        System.out.print("Teacher login: ");
        String tLogin = scanner.nextLine().trim();
        Teacher teacher = teachers.stream()
                .filter(t -> t.getLogin().equalsIgnoreCase(tLogin))
                .findFirst().orElse(null);
        if (teacher == null) { System.out.println("Teacher not found."); return; }

        try {
            student.setSupervisor(teacher);
            System.out.println("Supervisor set: " + teacher.getFullName());
        } catch (LowHIndexException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================================
    // 1. КАБИНЕТ АДМИНИСТРАТОРА
    // =========================================================================
    private static void runAdminMenu(Admin admin) {
        System.out.println("\n--- [Панель Администратора] ---");
        System.out.println("1. Добавить пользователя (Add User)");
        System.out.println("2. Удалить пользователя (Remove User)");
        System.out.println("3. Изменить данные пользователя (Update User)");
        System.out.println("4. Просмотреть системный журнал логов (See Logs)");
        System.out.println("5. Выйти из системы (Logout)");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleCreateUserMenu(); // ИСПРАВЛЕНО: Вместо заглушки вызываем интерактивное меню
            case "2" -> handleRemoveUser();
            case "3" -> handleUpdateUser();
            case "4" -> printLogsFromFile();
            case "5" -> currentUser = null;
            default -> System.out.println("Неверный выбор.");
        }
    }
    
    private static void handleRemoveUser() {
        System.out.println("\n--- Remove User ---");
        university.getUsers().forEach(u ->
            System.out.printf("  ID: %-8s | %s (%s)%n",
                u.getId(), u.getFullName(), u.getClass().getSimpleName()));
        System.out.print("Enter User ID to remove: ");
        String id = scanner.nextLine().trim();
        boolean removed = university.removeUser(id);
        System.out.println(removed ? "User removed successfully." : "User not found.");
    }

    private static void handleUpdateUser() {
        System.out.println("\n--- Update User ---");
        university.getUsers().forEach(u ->
            System.out.printf("  ID: %-8s | %s | login: %s%n",
                u.getId(), u.getFullName(), u.getLogin()));
        System.out.print("Enter User ID to update: ");
        String id = scanner.nextLine().trim();
        User target = university.findUserById(id);
        if (target == null) { System.out.println("User not found."); return; }

        System.out.println("What to update?");
        System.out.println("1. First Name");
        System.out.println("2. Last Name");
        System.out.println("3. Email");
        System.out.println("4. Password");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();
        System.out.print("New value: ");
        String val = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> target.setFirstName(val);
            case "2" -> target.setLastName(val);
            case "3" -> target.setEmail(val);
            case "4" -> target.setPassword(val);
            default  -> { System.out.println("Invalid choice."); return; }
        }
        System.out.println("Updated: " + target.getFullName());
    }
    // =========================================================================
    // 2. КАБИНЕТ МЕНЕДЖЕРА
    // =========================================================================
    private static void runManagerMenu(Manager manager) {
    	System.out.println("1. Назначить курс преподавателю (Assign teacher)");
    	System.out.println("2. Создать новый учебный курс (Add Course)");
    	System.out.println("3. Одобрить регистрацию студента");
    	System.out.println("4. Сгенерировать отчет успеваемости");
    	System.out.println("5. Просмотреть студентов по GPA");
    	System.out.println("6. Просмотреть студентов по алфавиту");
    	System.out.println("7. Управление новостями");
    	System.out.println("8. Просмотреть заявки сотрудников");
    	System.out.println("9. Официальные сообщения");
    	System.out.println("10. Отправить сообщение");
    	System.out.println("11. Посмотреть почтовый ящик");
    	System.out.println("12. Выйти из системы (Logout)");

    
        switch (scanner.nextLine().trim()) {
        case "1"  -> handleAssignTeacher(manager);
        case "2"  -> {
            System.out.println("\n[Создание нового курса]:");
            System.out.print("Введите код курса (например, CSCI2102): "); String code = scanner.nextLine().trim();
            System.out.print("Введите название курса: "); String name = scanner.nextLine().trim();
            Course newCourse = new Course(code, name, 3, CourseType.MAJOR);
            university.addCourse(newCourse);
            System.out.println("Курс успешно добавлен.");
        }
        case "3"  -> handleApproveRegistration(manager);
        case "4"  -> {
            List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u).toList();
            manager.createReport(students);
        }
        case "5"  -> {
            List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u).toList();
            manager.viewStudentsByGpa(students);
        }
        case "6"  -> {
            List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u).toList();
            manager.viewStudentsAlphabetically(students);
        }
        case "7"  -> handleManageNews(manager);
        case "8"  -> manager.viewRequests(university.getRequests());
        case "9"  -> handleOfficialMessages(manager);
        case "10" -> handleSendMessageMenu(manager);
        case "11" -> handleViewMailboxMenu(manager);
        case "12" -> { currentUser = null; System.out.println("Logged out."); }
        default   -> System.out.println("Неверный выбор.");
        }
    }
    
    private static void handleOfficialMessages(Manager manager) {
        System.out.println("\n--- Официальные сообщения ---");
        System.out.println("1. Создать сообщение");
        System.out.println("2. Просмотреть все сообщения");
        System.out.print("Выберите действие: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.print("Тема: ");
                String subject = scanner.nextLine().trim();
                System.out.print("Текст (например: Бронирование ауд. 302 для экзамена): ");
                String body = scanner.nextLine().trim();
                OfficialMessage msg = new OfficialMessage(
                        "OM-" + System.currentTimeMillis(), subject, body, manager);
                university.addOfficialMessage(msg);
                System.out.println("Сообщение создано.");
            }
            case "2" -> {
                List<OfficialMessage> msgs = university.getOfficialMessages();
                if (msgs.isEmpty()) { System.out.println("Нет сообщений."); return; }
                msgs.forEach(System.out::println);
            }
            default -> System.out.println("Неверный выбор.");
        }
    }
    private static void handleApproveRegistration(Manager manager) {
        System.out.println("\n--- Approve Student Registration ---");
        List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u).toList();
        if (students.isEmpty()) { System.out.println("No students."); return; }
        students.forEach(s -> System.out.printf("  login: %-15s | %s%n",
                s.getLogin(), s.getFullName()));

        System.out.print("Student login: ");
        String sLogin = scanner.nextLine().trim();
        Student student = students.stream()
                .filter(s -> s.getLogin().equalsIgnoreCase(sLogin))
                .findFirst().orElse(null);
        if (student == null) { System.out.println("Student not found."); return; }

        System.out.println("Courses:");
        university.getCourses().forEach(c -> System.out.println("  " + c));
        System.out.print("Course code: ");
        String code = scanner.nextLine().trim();
        Course course = university.getCourses().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst().orElse(null);
        if (course == null) { System.out.println("Course not found."); return; }

        manager.approveRegistration(student, course);
    }

    private static void handleManageNews(Manager manager) {
        System.out.println("\n--- Manage News ---");
        System.out.println("1. Add news");
        System.out.println("2. View all news");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) {
            System.out.print("Title: ");
            String title = scanner.nextLine().trim();
            System.out.print("Content: ");
            String content = scanner.nextLine().trim();
            System.out.print("Topic (RESEARCH / GENERAL): ");
            String topic = scanner.nextLine().trim();
            News news = new News(title, content, topic);
            university.addNews(news);
            System.out.println("News added. Pinned: " + news.isPinned());
        } else if (choice.equals("2")) {
            List<News> newsList = new java.util.ArrayList<>(university.getNews());
            java.util.Collections.sort(newsList);
            if (newsList.isEmpty()) { System.out.println("No news."); return; }
            newsList.forEach(System.out::println);
        }
    } 
    
    private static void handleAssignTeacher(Manager manager) {
        System.out.println("\n--- Assign Teacher to Course ---");
        List<Teacher> teachers = university.getUsers().stream()
                .filter(u -> u instanceof Teacher)
                .map(u -> (Teacher) u)
                .toList();
        if (teachers.isEmpty()) { System.out.println("No teachers in system."); return; }
        teachers.forEach(t -> System.out.printf("  login: %-15s | %s | %s%n",
                t.getLogin(), t.getFullName(), t.getPosition()));

        System.out.println("Courses:");
        university.getCourses().forEach(c -> System.out.println("  " + c));

        System.out.print("Teacher login: ");
        String tLogin = scanner.nextLine().trim();
        Teacher teacher = teachers.stream()
                .filter(t -> t.getLogin().equalsIgnoreCase(tLogin))
                .findFirst().orElse(null);
        if (teacher == null) { System.out.println("Teacher not found."); return; }

        System.out.print("Course code: ");
        String code = scanner.nextLine().trim();
        Course course = university.getCourses().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst().orElse(null);
        if (course == null) { System.out.println("Course not found."); return; }

        System.out.println("Lesson type: 1. LECTURE  2. PRACTICE");
        System.out.print("Choice: ");
        String lessonChoice = scanner.nextLine().trim();
        LessonType lessonType = lessonChoice.equals("2") ? LessonType.PRACTICE : LessonType.LECTURE;
        course.addTeacher(teacher, lessonType);
        teacher.addCourse(course);
        System.out.printf("Teacher %s assigned to %s as %s instructor.%n",
                teacher.getFullName(), course.getName(), lessonType);
    }

    // =========================================================================
    // 3. КАБИНЕТ ПРЕПОДАВАТЕЛЯ
    // =========================================================================
    private static void runTeacherMenu(Teacher teacher) {
        System.out.println("\n--- [Панель Преподавателя] ---");
        System.out.println("1. Просмотреть мои учебные дисциплины");
        System.out.println("2. Посмотреть список студентов на моих курсах");
        System.out.println("3. Отправить сообщение (Send Message)");
        System.out.println("4. Проверить почтовый ящик (Mailbox)");
        System.out.println("5. Выставить оценку студенту (Put Mark)");
        System.out.println("6. Отправить жалобу в деканат (Send Complaint)");
        System.out.println("7. Мои научные статьи (Research)");
        System.out.println("8. Выйти из системы (Logout)");
        System.out.print("Выберите действие: ");

        
        switch (scanner.nextLine().trim()) {
            case "1" -> teacher.viewCourses();
            case "2" -> teacher.viewStudents();
            case "3" -> handleSendMessageMenu(teacher);
            case "4" -> handleViewMailboxMenu(teacher);
            case "5" -> handlePutMark(teacher);
            case "6" -> handleSendComplaint(teacher);
            case "7" -> handleResearcherMenu(teacher);
            case "8" -> { currentUser = null; System.out.println("Logged out."); }
            default -> System.out.println("Неверный выбор.");
        }
    }
    
    private static void handleResearcherMenu(Researcher researcher) {
    	System.out.println("\n--- Меню Исследователя ---");
    	System.out.println("1. Мои статьи");
    	System.out.println("2. Добавить статью");
    	System.out.println("3. Посмотреть h-index");
    	System.out.println("4. Статьи по цитированиям");
    	System.out.println("5. Все статьи университета");
    	System.out.println("6. Топ цитируемый исследователь года");
    	System.out.println("7. Создать исследовательский проект");
    	System.out.println("8. Просмотреть мои проекты");
    	System.out.println("9. Назад");
    	System.out.print("Выберите действие: ");
        switch (scanner.nextLine().trim()) {
            case "1" -> {
                List<university.research.ResearchPaper> papers = researcher.getPapers();
                if (papers.isEmpty()) { System.out.println("No papers yet."); return; }
                papers.forEach(System.out::println);
            }
            case "2" -> {
                try {
                    System.out.print("Title: ");
                    String title = scanner.nextLine().trim();
                    System.out.print("Authors (comma separated): ");
                    List<String> authors = Arrays.asList(scanner.nextLine().split(","));
                    System.out.print("Journal: ");
                    String journal = scanner.nextLine().trim();
                    System.out.print("Start page: ");
                    int start = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("End page: ");
                    int end = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Year (e.g. 2024): ");
                    int year = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("DOI: ");
                    String doi = scanner.nextLine().trim();
                    System.out.print("Citations: ");
                    int citations = Integer.parseInt(scanner.nextLine().trim());

                    university.research.ResearchPaper paper = new university.research.ResearchPaper(
                            title, authors, journal, start, end,
                            LocalDate.of(year, 1, 1), doi, citations);
                    ResearchManager.getInstance().registerResearcher(researcher);
                    researcher.addPaper(paper);
                    System.out.println("Paper added successfully.");
                    
                    // Автоновость о публикации статьи
                    String authorName = (researcher instanceof User) ? ((User) researcher).getFullName() : "Researcher";
                    News paperNews = new News(
                        "New Research Paper Published",
                        authorName + " published: \"" + title + "\" in " + journal,
                        "RESEARCH"
                    );
                    university.addNews(paperNews);
                    System.out.println("Announcement created automatically.");

                    // Автоновость про топ cited researcher
                    ResearchManager rm = ResearchManager.getInstance();
                    Researcher top = rm.getTopCitedResearcher();
                    if (top != null) {
                        String topName = (top instanceof User) ? ((User) top).getFullName() : "Unknown";
                        News topNews = new News(
                            "Top Cited Researcher",
                            topName + " is the most cited researcher at KBTU with h-index: " + top.calculateHIndex(),
                            "RESEARCH"
                        );
                        university.addNews(topNews);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: enter a number.");
                }
            }
            case "3" -> System.out.println("Your h-index: " + researcher.calculateHIndex());
            case "4" -> {
                Comparator<university.research.ResearchPaper> byCitations =
                    (p1, p2) -> Integer.compare(p2.getCitations(), p1.getCitations());
                researcher.printPapers(byCitations);
            }
            case "5" -> {
                ResearchManager rm = ResearchManager.getInstance();
                System.out.println("Сортировка: 1. По цитированиям  2. По дате  3. По длине");
                System.out.print("Выберите: ");
                String s = scanner.nextLine().trim();
                Comparator<university.research.ResearchPaper> comp = switch (s) {
                    case "2" -> new university.research.comparators.PaperByDateComparator();
                    case "3" -> new university.research.comparators.PaperByLengthComparator();
                    default  -> new university.research.comparators.PaperByCitationsComparator();
                };
                rm.printAllPapers(comp);
            }
            case "6" -> {
                System.out.print("Enter year (e.g. 2024): ");
                try {
                    int year = Integer.parseInt(scanner.nextLine().trim());
                    ResearchManager.getInstance().printTopCitedResearcherOfYear(year);
                } catch (NumberFormatException e) {
                    System.out.println("Error: enter a number.");
                }
            }
            case "7" -> handleCreateResearchProject(researcher);
            case "8" -> {
                List<university.research.ResearchProject> projects = researcher.getProjects();
                if (projects.isEmpty()) { System.out.println("No projects yet."); return; }
                projects.forEach(System.out::println);
            }
            case "9" -> { return; }
            default  -> System.out.println("Invalid choice.");
        }
    }
    
    private static void handleCreateResearchProject(Researcher researcher) {
        System.out.println("\n--- Create Research Project ---");
        System.out.print("Project ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Topic: ");
        String topic = scanner.nextLine().trim();
        if (id.isEmpty() || topic.isEmpty()) {
            System.out.println("ID and topic cannot be empty.");
            return;
        }

        university.research.ResearchProject project =
                new university.research.ResearchProject(id, topic);

        try {
            project.addParticipant(researcher);
        } catch (university.exceptions.NotAResearcherException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        researcher.addProject(project);
        System.out.println("Project created: " + project);

        // Предложить добавить других участников
        System.out.print("Add another researcher? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            List<Teacher> teachers = university.getUsers().stream()
                    .filter(u -> u instanceof Teacher)
                    .map(u -> (Teacher) u)
                    .toList();
            teachers.forEach(t -> System.out.printf("  login: %-15s | %s | h-index: %d%n",
                    t.getLogin(), t.getFullName(), t.calculateHIndex()));
            System.out.print("Teacher login: ");
            String tLogin = scanner.nextLine().trim();
            Teacher teacher = teachers.stream()
                    .filter(t -> t.getLogin().equalsIgnoreCase(tLogin))
                    .findFirst().orElse(null);
            if (teacher == null) { System.out.println("Teacher not found."); return; }
            try {
                project.addParticipant(teacher);
                System.out.println("Added: " + teacher.getFullName());
            } catch (university.exceptions.NotAResearcherException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private static void handlePutMark(Teacher teacher) {
        System.out.println("\n--- Put Mark ---");
        List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .toList();
        if (students.isEmpty()) { System.out.println("No students in system."); return; }
        students.forEach(s -> System.out.printf("  login: %-15s | %s%n", s.getLogin(), s.getFullName()));

        System.out.print("Student login: ");
        String sLogin = scanner.nextLine().trim();
        Student student = students.stream()
                .filter(s -> s.getLogin().equalsIgnoreCase(sLogin))
                .findFirst().orElse(null);
        if (student == null) { System.out.println("Student not found."); return; }

        System.out.println("Courses:");
        university.getCourses().forEach(c -> System.out.println("  " + c));
        System.out.print("Course code: ");
        String code = scanner.nextLine().trim();
        Course course = university.getCourses().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst().orElse(null);
        if (course == null) { System.out.println("Course not found."); return; }

        try {
            System.out.print("Attestation 1 (0-30): ");
            double att1 = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Attestation 2 (0-30): ");
            double att2 = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Final exam (0-40): ");
            double fin = Double.parseDouble(scanner.nextLine().trim());

            Mark mark = new Mark(course, student);
            mark.setAtt1(att1);
            mark.setAtt2(att2);
            mark.setFinalExam(fin);
            teacher.putMark(student, course, mark);
            System.out.printf("Mark saved: %.1f (%s)%n", mark.getTotal(), mark.getLetterGrade());
        } catch (NumberFormatException e) {
            System.out.println("Error: enter a number.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleSendComplaint(Teacher teacher) {
        System.out.println("\n--- Send Complaint to Dean ---");
        List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .toList();
        if (students.isEmpty()) { System.out.println("No students in system."); return; }
        students.forEach(s -> System.out.printf("  login: %-15s | %s%n", s.getLogin(), s.getFullName()));

        System.out.print("Student login: ");
        String sLogin = scanner.nextLine().trim();
        Student student = students.stream()
                .filter(s -> s.getLogin().equalsIgnoreCase(sLogin))
                .findFirst().orElse(null);
        if (student == null) { System.out.println("Student not found."); return; }

        List<Manager> managers = university.getUsers().stream()
                .filter(u -> u instanceof Manager)
                .map(u -> (Manager) u)
                .toList();
        if (managers.isEmpty()) { System.out.println("No managers in system."); return; }
        Manager dean = managers.get(0);

        System.out.println("Urgency: 1. LOW  2. MEDIUM  3. HIGH");
        System.out.print("Choice: ");
        String urg = scanner.nextLine().trim();
        UrgencyLevel level = switch (urg) {
            case "2" -> UrgencyLevel.MEDIUM;
            case "3" -> UrgencyLevel.HIGH;
            default  -> UrgencyLevel.LOW;
        };

        teacher.sendComplaint(student, dean, level);
    }
    
    

    // =========================================================================
    // 4. ПОЛНОЦЕННЫЙ КАБИНЕТ СТУДЕНТА (ВОССТАНОВЛЕНО НА 100%)
    // =========================================================================
    private static void runStudentMenu(Student student) {
        System.out.println("\n--- [Личный кабинет Студента] ---");
        System.out.println("1. Зарегистрироваться на учебный курс");
        System.out.println("2. Посмотреть мои текущие оценки (View Marks)");
        System.out.println("3. Распечатать академический транскрипт и GPA");
        System.out.println("4. Написать сообщение (Студенческие организации/Коллеги)");
        System.out.println("5. Посмотреть мой почтовый ящик (Mailbox)");
        System.out.println("6. Оценить преподавателя (Rate Teacher)");
        System.out.println("7. Студенческие организации");
        System.out.println("8. Подписаться на журнал");
        System.out.println("9. Сменить язык (Switch Language)");
        System.out.println("10. Выйти из системы (Logout)");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.println("\n--- Доступные курсы для регистрации ---");
                university.getCourses().forEach(System.out::println);
                System.out.print("Введите код выбранного курса: ");
                String code = scanner.nextLine().trim();
                Course course = university.getCourses().stream()
                        .filter(c -> c.getCode().equalsIgnoreCase(code))
                        .findFirst().orElse(null);

                if (course == null) {
                    System.out.println("Ошибка: Курс не найден.");
                    return;
                }
                try {
                    // Бизнес-логика контроля лимитов (21 кредит, 3 завала)
                    student.registerCourse(course);
                    System.out.println("Регистрация на дисциплину успешно завершена.");
                } catch (MaxCreditsException | CourseFailLimitException ex) {
                    System.out.println("Регистрация отклонена системой: " + ex.getMessage());
                }
            }
            case "2" -> student.viewMarks();
            case "3" -> System.out.println(student.getTranscript().toString());
            case "4" -> handleSendMessageMenu(student);
            case "5" -> handleViewMailboxMenu(student);
            case "6" -> handleRateTeacher(student);
            case "7" -> handleStudentOrganizations(student);
            case "8" -> handleJournalSubscription(student);
            case "9" -> handleSwitchLanguage(currentUser);
            case "10" -> { currentUser = null; System.out.println("Logged out."); }
            default -> System.out.println("Неверный выбор.");
        }
    }
    
    private static void handleJournalSubscription(User user) {
        System.out.println("\n--- University Journals ---");
        List<university.research.journal.UniversityJournal> journals = university.getJournals();
        if (journals.isEmpty()) { System.out.println("No journals available."); return; }

        journals.forEach(j -> System.out.printf("  ID: %-8s | %s | ISSN: %s | papers: %d%n",
                j.getJournalId(), j.getName(), j.getIssn(), j.getPapers().size()));

        System.out.println("1. Подписаться на журнал");
        System.out.println("2. Back");
        System.out.print("Choice: ");

        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.print("Journal ID: ");
        String jid = scanner.nextLine().trim();
        university.research.journal.UniversityJournal journal = journals.stream()
                .filter(j -> j.getJournalId().equalsIgnoreCase(jid))
                .findFirst().orElse(null);
        if (journal == null) { System.out.println("Journal not found."); return; }

        journal.subscribe((paper, j) -> System.out.printf(
                "[%s] Уведомление: новая статья '%s' опубликована в журнале '%s'%n",
                user.getFullName(), paper.getTitle(), j.getName()));
        System.out.println("Вы подписаны на: " + journal.getName());
    }
    
    
    
    private static void handleStudentOrganizations(Student student) {
        System.out.println("\n--- Student Organizations ---");
        System.out.println("1. Просмотреть все организации");
        System.out.println("2. Вступить в организацию");
        System.out.println("3. Создать организацию");
        System.out.println("4. Покинуть организацию");
        System.out.print("Choice: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                List<StudentOrganization> orgs = university.getOrganizations();
                if (orgs.isEmpty()) { System.out.println("No organizations yet."); return; }
                orgs.forEach(System.out::println);
            }
            case "2" -> {
                List<StudentOrganization> orgs = university.getOrganizations();
                if (orgs.isEmpty()) { System.out.println("No organizations yet."); return; }
                orgs.forEach(o -> System.out.printf("  %d. %s%n",
                        orgs.indexOf(o) + 1, o.getName()));
                System.out.print("Enter organization name: ");
                String name = scanner.nextLine().trim();
                StudentOrganization org = orgs.stream()
                        .filter(o -> o.getName().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
                if (org == null) { System.out.println("Organization not found."); return; }
                org.addMember(student);
            }
            case "3" -> {
                System.out.print("Organization name: ");
                String name = scanner.nextLine().trim();
                if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }
                StudentOrganization org = new StudentOrganization(name, student);
                university.addOrganization(org);
                System.out.println("Organization '" + name + "' created. You are the head.");
            }
            case "4" -> {
                List<StudentOrganization> orgs = university.getOrganizations();
                if (orgs.isEmpty()) { System.out.println("No organizations."); return; }
                System.out.print("Enter organization name: ");
                String name = scanner.nextLine().trim();
                StudentOrganization org = orgs.stream()
                        .filter(o -> o.getName().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
                if (org == null) { System.out.println("Organization not found."); return; }
                org.removeMember(student);
            }
            default -> System.out.println("Invalid choice.");
        }
    }
    
    private static void handleSwitchLanguage(User user) {
        System.out.println("\n--- Switch Language ---");
        System.out.println("1. English (EN)");
        System.out.println("2. Казахский (KZ)");
        System.out.println("3. Русский (RU)");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();
        String lang = switch (choice) {
            case "2" -> "KZ";
            case "3" -> "RU";
            default  -> "EN";
        };
        user.switchLanguage(lang);
        System.out.println("Language switched to: " + lang);
    }
    
    private static void handleRateTeacher(Student student) {
        System.out.println("\n--- Rate Teacher ---");
        List<Teacher> teachers = university.getUsers().stream()
                .filter(u -> u instanceof Teacher)
                .map(u -> (Teacher) u)
                .toList();
        if (teachers.isEmpty()) { System.out.println("No teachers in system."); return; }
        teachers.forEach(t -> System.out.printf("  login: %-15s | %s | avg rating: %.1f%n",
                t.getLogin(), t.getFullName(), t.getRating()));

        System.out.print("Teacher login: ");
        String tLogin = scanner.nextLine().trim();
        Teacher teacher = teachers.stream()
                .filter(t -> t.getLogin().equalsIgnoreCase(tLogin))
                .findFirst().orElse(null);
        if (teacher == null) { System.out.println("Teacher not found."); return; }

        System.out.print("Rating (1-10): ");
        try {
            int rating = Integer.parseInt(scanner.nextLine().trim());
            student.rateTeacher(teacher, rating);
            System.out.printf("Done! %s new average rating: %.1f%n",
                    teacher.getFullName(), teacher.getRating());
        } catch (NumberFormatException e) {
            System.out.println("Error: enter a number.");
        }
    }

    // =========================================================================
    // 5. КАБИНЕТ ТЕХНИЧЕСКОЙ ПОДДЕРЖКИ
    // =========================================================================
    private static void runTechSupportMenu(TechSupportSpecialist techSupport) {
        System.out.println("\n--- [Панель Технической Поддержки] ---");
        System.out.println("1. Проверить системный журнал логов (View System Logs)");
        System.out.println("2. Просмотреть входящие заявки (Requests)");
        System.out.println("3. Создать новую заявку (New Request)");
        System.out.println("4. Выйти из системы (Logout)");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
        case "1" -> printLogsFromFile();
        case "2" -> handleRequestsMenu();
        case "3" -> handleCreateRequest(techSupport);
        case "4" -> { currentUser = null; System.out.println("Logged out."); }
            default -> System.out.println("Неверный выбор.");
        }
    }
    
    private static void handleCreateRequest(User sender) {
        System.out.println("\n--- Create New Request ---");
        System.out.print("Describe the problem: ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) { System.out.println("Description cannot be empty."); return; }
        Request request = new Request(sender, description);
        university.addRequest(request);
        System.out.println("Request created: " + request.getId());
    }

    // =========================================================================
    // СИСТЕМНЫЕ МЕТОДЫ ПОЧТЫ И УПРАВЛЕНИЯ ЗАЯВКАМИ
    // =========================================================================
    private static void handleSendMessageMenu(User sender) {
        System.out.print("\nВведите логин получателя (admin, manager, teacher, student, tech): ");
        String receiverLogin = scanner.nextLine().trim();

        User receiver = university.getUsers().stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(receiverLogin))
                .findFirst().orElse(null);

        if (receiver == null) {
            System.out.println("Ошибка: Пользователь не найден.");
            return;
        }

        System.out.print("Введите текст сообщения: ");
        String text = scanner.nextLine().trim();
        university.sendMessage(sender, receiver, text);
        System.out.println("✅ Сообщение успешно отправлено.");
    }

    private static void handleViewMailboxMenu(User user) {
        System.out.println("\n=== ВХОДЯЩИЕ СООБЩЕНИЯ ===");
        var myMessages = university.getMessagesForUser(user);

        if (myMessages.isEmpty()) {
            System.out.println("Ваш электронный ящик пуст.");
            return;
        }

        for (int i = 0; i < myMessages.size(); i++) {
            var m = myMessages.get(i);
            System.out.printf("%d. %s от %s (%s)%n", i + 1, m.isRead() ? "[Прочитано]" : "[📌 НОВОЕ]", m.getSender().getFullName(), m.getSentAt());
        }

        System.out.print("Введите номер сообщения для чтения (или Enter для отмены): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;

        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < myMessages.size()) {
                var selectedMsg = myMessages.get(index);
                selectedMsg.markAsRead();
                System.out.printf("%n--- Текст письма ---%nОт: %s%nТекст: %s%n--------------------%n", selectedMsg.getSender().getFullName(), selectedMsg.getText());
            }
        } catch (Exception e) {
            System.out.println("Ошибка ввода.");
        }
    }

    private static void handleRequestsMenu() {
        System.out.println("\n=== ОЧЕРЕДЬ ЗАЯВЛЕНИЙ ТЕХПОДДЕРЖКИ ===");
        var requests = university.getRequests();

        if (requests == null || requests.isEmpty()) {
            System.out.println("Активных заявок на ремонт нет.");
            return;
        }

        requests.forEach(r -> { r.view(); System.out.println(r); });

        System.out.print("Введите ID заявки для обработки (или Enter): ");
        String reqId = scanner.nextLine().trim();
        if (reqId.isEmpty()) return;

        Request targetRequest = requests.stream().filter(r -> r.getId().equalsIgnoreCase(reqId)).findFirst().orElse(null);
        if (targetRequest == null) { System.out.println("Заявка не найдена."); return; }

        System.out.println("1. Принять (ACCEPT) | 2. Отклонить (REJECT) | 3. Выполнить (DONE)");
        String act = scanner.nextLine().trim();
        if (act.equals("1")) targetRequest.accept();
        else if (act.equals("2")) targetRequest.reject();
        else if (act.equals("3")) targetRequest.markAsDone();
        System.out.println("Статус заявки успешно обновлен.");
    }

    private static void printLogsFromFile() {
        File logFile = new File("university.log");
        if (!logFile.exists() || logFile.length() == 0) {
            System.out.println("Журнал системных логов пуст.");
            return;
        }
        System.out.println("\n=== ВЫЧИТКА ЛОГ-ФАЙЛА (UNIVERSITY.LOG) ===");
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) System.out.println(line);
        } catch (IOException e) {
            System.out.println("Ошибка чтения: " + e.getMessage());
        }
        System.out.println("============================================");
    }

    private static void handleCreateUserMenu() {
        System.out.println("\n--- СОЗДАНИЕ НОВОГО ПОЛЬЗОВАТЕЛЯ ---");
        System.out.println("1. Студент (Student)");
        System.out.println("2. Магистр (GraduateStudent)");
        System.out.println("3. Преподаватель (Teacher)");
        System.out.println("4. Академический Менеджер (Manager)");
        System.out.println("5. Специалист Техподдержки (Tech Support)");
        System.out.println("6. Системный Администратор (Admin)");
        System.out.print("Ваш выбор (1-6): ");
        String roleChoice = scanner.nextLine().trim();

        System.out.print("Введите уникальный ID (например, S124, T002): ");
        String id = scanner.nextLine().trim();
        System.out.print("Введите Имя: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Введите Фамилию: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Введите Электронную почту: ");
        String email = scanner.nextLine().trim();
        System.out.print("Придумайте уникальный Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Придумайте Пароль: ");
        String password = scanner.nextLine().trim();

        boolean loginExists = university.getUsers().stream()
                .anyMatch(u -> u.getLogin().equalsIgnoreCase(login));
        if (loginExists) {
            System.out.println("Ошибка: логин '" + login + "' уже существует!");
            return;
        }

        User newUser = null;
        try {
            switch (roleChoice) {
                case "1" -> {
                    System.out.print("Факультет (Major): ");
                    String major = scanner.nextLine().trim();
                    System.out.print("Курс обучения (1-4): ");
                    int year = Integer.parseInt(scanner.nextLine().trim());
                    newUser = UserFactory.createStudent(id, firstName, lastName, email, login, password, major, year);
                }
                case "2" -> {
                    System.out.print("Факультет (Major): ");
                    String major = scanner.nextLine().trim();
                    System.out.print("Курс обучения (1-2): ");
                    int year = Integer.parseInt(scanner.nextLine().trim());
                    System.out.println("Тип степени: 1. MASTER  2. PHD");
                    System.out.print("Ваш выбор: ");
                    String degreeChoice = scanner.nextLine().trim();
                    DegreeType degree = degreeChoice.equals("2") ? DegreeType.PHD : DegreeType.MASTER;
                    newUser = new GraduateStudent(id, firstName, lastName, email, login, password, major, year, degree);
                }
                case "3" -> {
                    System.out.print("Оклад (Salary): ");
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Кафедра (Department): ");
                    String dept = scanner.nextLine().trim();
                    System.out.println("Должность: 1. TUTOR  2. LECTOR  3. SENIOR_LECTOR  4. PROFESSOR");
                    System.out.print("Ваш выбор: ");
                    String posChoice = scanner.nextLine().trim();
                    TeacherPosition position = switch (posChoice) {
                        case "1" -> TeacherPosition.TUTOR;
                        case "3" -> TeacherPosition.SENIOR_LECTOR;
                        case "4" -> TeacherPosition.PROFESSOR;
                        default  -> TeacherPosition.LECTOR;
                    };
                    newUser = UserFactory.createTeacher(id, firstName, lastName, email, login, password, salary, dept, position);
                }
                case "4" -> {
                    System.out.print("Оклад (Salary): ");
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    System.out.println("Тип офиса: 1. OR  2. DEPARTMENT  3. DEAN_OFFICE");
                    System.out.print("Ваш выбор: ");
                    String typeChoice = scanner.nextLine().trim();
                    ManagerType mType = switch (typeChoice) {
                        case "2" -> ManagerType.DEPARTMENT;
                        case "3" -> ManagerType.DEAN_OFFICE;
                        default  -> ManagerType.OR;
                    };
                    newUser = UserFactory.createManager(id, firstName, lastName, email, login, password, salary, mType);
                }
                case "5" -> {
                    System.out.print("Оклад (Salary): ");
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    newUser = UserFactory.createTechSupport(id, firstName, lastName, email, login, password, salary);
                }
                case "6" -> {
                    System.out.print("Оклад (Salary): ");
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    newUser = UserFactory.createAdmin(id, firstName, lastName, email, login, password, salary);
                }
                default -> {
                    System.out.println("Неверная роль.");
                    return;
                }
            }

            if (newUser != null) {
                university.addUser(newUser);
                System.out.printf("Пользователь %s (%s) добавлен.%n",
                        newUser.getFullName(), newUser.getClass().getSimpleName());
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите числовое значение.");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}