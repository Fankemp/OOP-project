package university.view;

import university.storage.University;
import university.academic.Course;
import university.users.Manager;
import university.users.Student;
import university.users.Teacher;
import university.communications.News;
import university.communications.Request;
import university.communications.OfficialMessage;
import university.enums.CourseType;
import university.enums.LessonType;

import java.util.List;
import java.util.Scanner;

/**
 * Изолированный обработчик интерфейса для роли Академического Менеджера.
 * Кодировка восстановлена, убрана зависимость от удаленных методов класса News.
 */
public class ManagerMenuHandler {
    private static final University university = University.getInstance();

    /**
     * Отображает меню менеджера и обрабатывает ввод.
     * @return true если пользователь выбрал Logout, иначе false.
     */
    public static boolean show(Manager manager, Scanner scanner) {
        System.out.println("\n--- [Панель Академического Менеджера KBTU] ---");
        System.out.println("1. Назначить преподавателя на курс (Assign teacher)");
        System.out.println("2. Создать новый учебный курс (Add Course)");
        System.out.println("3. Одобрить регистрацию студента (Approve Registration)");
        System.out.println("4. Сгенерировать отчет по успеваемости студентов");
        System.out.println("5. Просмотреть список студентов KBTU по GPA");
        System.out.println("6. Просмотреть список студентов по алфавиту");
        System.out.println("7. Управление новостной лентой (Manage News)");
        System.out.println("8. Просмотреть заявления (Только подписанные Деканатом)");
        System.out.println("9. Управление официальными сообщениями вуза");
        System.out.println("10. Отправить личное сообщение (Send Message)");
        System.out.println("11. Посмотреть мой почтовый ящик (Mailbox)");
        System.out.println("12. Выйти из системы (Logout)");
        System.out.println("13. Отправить заявку в техподдержку (Send Tech Request)");
        System.out.print("Выберите действие: ");

        switch (scanner.nextLine().trim()) {
            case "1"  -> handleAssignTeacher(manager, scanner);
            case "2"  -> {
                System.out.println("\n[Создание нового курса]:");
                System.out.print("Введите код курса (например, CSCI2102): ");
                String code = scanner.nextLine().trim();
                System.out.print("Введите название курса: ");
                String name = scanner.nextLine().trim();
                System.out.print("Введите количество кредитов (Credits, например, 5): ");
                int credits = Integer.parseInt(scanner.nextLine().trim());

                Course newCourse = new Course(code, name, credits, CourseType.MAJOR);
                university.addCourse(newCourse);
                System.out.println("Курс успешно создан.");
            }
            case "3"  -> handleApproveRegistration(manager, scanner);
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
            case "7"  -> handleManageNews(manager, scanner);
            case "8"  -> {
                List<Request> signedRequests = university.getRequests().stream()
                        .filter(Request::isSigned)
                        .toList();
                manager.viewRequests(signedRequests);
            }
            case "9"  -> handleOfficialMessages(manager, scanner);

            // Использование общего CommunicationHandler
            case "10" -> CommunicationHandler.handleSendMessageMenu(manager, scanner);
            case "11" -> CommunicationHandler.handleViewMailboxMenu(manager, scanner);

            case "12" -> {
                System.out.println("Сессия менеджера успешно завершена.");
                return true;
            }
            case "13" -> TechSupportMenuHandler.handleCreateRequest(manager, scanner);
            default   -> System.out.println("Ошибка: неверный пункт меню.");
        }
        return false;
    }

    private static void handleOfficialMessages(Manager manager, Scanner scanner) {
        System.out.println("\n--- Управление официальными сообщениями вуза ---");
        System.out.println("1. Создать официальное объявление / Бронь кабинета");
        System.out.println("2. Просмотреть архив официальных сообщений");
        System.out.print("Выберите действие: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.print("Тема сообщения (Subject): ");
                String subject = scanner.nextLine().trim();
                System.out.print("Текст (например: 'Забронирована ауд. 302 под экзамен'): ");
                String body = scanner.nextLine().trim();

                OfficialMessage msg = new OfficialMessage(
                        "OM-" + System.currentTimeMillis(), subject, body, manager);
                university.addOfficialMessage(msg);
                System.out.println("Официальное сообщение успешно создано.");
            }
            case "2" -> {
                List<OfficialMessage> msgs = university.getOfficialMessages();
                if (msgs.isEmpty()) { System.out.println("Архив официальных сообщений пуст."); return; }
                msgs.forEach(System.out::println);
            }
            default -> System.out.println("Неверный выбор.");
        }
    }

    private static void handleApproveRegistration(Manager manager, Scanner scanner) {
        System.out.println("\n--- Approve Student Registration ---");
        List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u).toList();
        if (students.isEmpty()) { System.out.println("No students."); return; }

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

        manager.approveRegistration(student, course);
    }

    private static void handleManageNews(Manager manager, Scanner scanner) {
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
            // ИСПРАВЛЕНО: Убрана упавшая ошибка компиляции с isPinned()
            System.out.println("Новость успешно добавлена в ленту.");
        } else if (choice.equals("2")) {
            List<News> newsList = new java.util.ArrayList<>(university.getNews());
            java.util.Collections.sort(newsList);

            if (newsList.isEmpty()) { System.out.println("No news."); return; }
            newsList.forEach(System.out::println);
        }
    }

    private static void handleAssignTeacher(Manager manager, Scanner scanner) {
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
}