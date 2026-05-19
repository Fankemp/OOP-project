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

public class ManagerMenuHandler {
    private static final University university = University.getInstance();

    public static boolean show(Manager manager, Scanner scanner) {
        System.out.println("\n--- [" + Lang.t("Панель Менеджера KBTU", "Manager Panel KBTU", "KBTU Менеджер панелі") + "] ---");
        System.out.println("1. "  + Lang.t("Назначить преподавателя на курс", "Assign Teacher", "Оқытушыны тағайындау"));
        System.out.println("2. "  + Lang.t("Создать новый курс", "Add Course", "Жаңа курс жасау"));
        System.out.println("3. "  + Lang.t("Одобрить регистрацию студента", "Approve Registration", "Тіркелуді растау"));
        System.out.println("4. "  + Lang.t("Отчёт по успеваемости", "Academic Report", "Үлгерім есебі"));
        System.out.println("5. "  + Lang.t("Студенты по GPA", "Students by GPA", "GPA бойынша студенттер"));
        System.out.println("6. "  + Lang.t("Студенты по алфавиту", "Students Alphabetically", "Алфавит бойынша студенттер"));
        System.out.println("7. "  + Lang.t("Управление новостями", "Manage News", "Жаңалықтарды басқару"));
        System.out.println("8. "  + Lang.t("Просмотреть заявки", "View Requests", "Өтінімдерді көру"));
        System.out.println("9. "  + Lang.t("Официальные сообщения", "Official Messages", "Ресми хабарламалар"));
        System.out.println("10. " + Lang.t("Отправить сообщение", "Send Message", "Хабар жіберу"));
        System.out.println("11. " + Lang.t("Почтовый ящик", "Mailbox", "Пошта жәшігі"));
        System.out.println("12. " + Lang.t("Выйти", "Logout", "Шығу"));
        System.out.println("13. " + Lang.t("Заявка в техподдержку", "Tech Request", "Техқолдауға өтініш"));
        System.out.println("14. " + Lang.t("Сменить язык", "Switch Language", "Тілді ауыстыру"));
        System.out.print(Lang.t("Выберите действие: ", "Choose action: ", "Әрекетті таңдаңыз: "));

        switch (scanner.nextLine().trim()) {
            case "1"  -> handleAssignTeacher(manager, scanner);
            case "2"  -> {
                System.out.print(Lang.t("Код курса: ", "Course code: ", "Курс коды: "));
                String code = scanner.nextLine().trim();
                System.out.print(Lang.t("Название: ", "Name: ", "Атауы: "));
                String name = scanner.nextLine().trim();
                System.out.print(Lang.t("Кредиты: ", "Credits: ", "Кредиттер: "));
                try {
                    int credits = Integer.parseInt(scanner.nextLine().trim());
                    university.addCourse(new Course(code, name, credits, CourseType.MAJOR));
                    System.out.println(Lang.t("✅ Курс создан.", "✅ Course created.", "✅ Курс жасалды."));
                } catch (NumberFormatException e) {
                    System.out.println(Lang.t("❌ Введите число.", "❌ Enter a number.", "❌ Сан енгізіңіз."));
                }
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
                        .filter(Request::isSigned).toList();
                manager.viewRequests(signedRequests);
            }
            case "9"  -> handleOfficialMessages(manager, scanner);
            case "10" -> CommunicationHandler.handleSendMessageMenu(manager, scanner);
            case "11" -> CommunicationHandler.handleViewMailboxMenu(manager, scanner);
            case "12" -> {
                System.out.println(Lang.t("Сессия завершена.", "Session ended.", "Сессия аяқталды."));
                return true;
            }
            case "13" -> TechSupportMenuHandler.handleCreateRequest(manager, scanner);
            case "14" -> SharedMenuComponents.handleSwitchLanguage(scanner);
            default   -> System.out.println(Lang.t("❌ Неверный выбор.", "❌ Invalid choice.", "❌ Жарамсыз таңдау."));
        }
        return false;
    }

    private static void handleOfficialMessages(Manager manager, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Официальные сообщения", "Official Messages", "Ресми хабарламалар") + " ---");
        System.out.println("1. " + Lang.t("Создать сообщение", "Create Message", "Хабарлама жасау"));
        System.out.println("2. " + Lang.t("Просмотреть архив", "View Archive", "Мұрағатты көру"));
        System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.print(Lang.t("Тема: ", "Subject: ", "Тақырып: "));
                String subject = scanner.nextLine().trim();
                System.out.print(Lang.t("Текст: ", "Body: ", "Мәтін: "));
                String body = scanner.nextLine().trim();
                university.addOfficialMessage(new OfficialMessage(
                        "OM-" + System.currentTimeMillis(), subject, body, manager));
                System.out.println(Lang.t("✅ Создано.", "✅ Created.", "✅ Жасалды."));
            }
            case "2" -> {
                List<OfficialMessage> msgs = university.getOfficialMessages();
                if (msgs.isEmpty()) {
                    System.out.println(Lang.t("Архив пуст.", "Archive is empty.", "Мұрағат бос."));
                    return;
                }
                msgs.forEach(System.out::println);
            }
            default -> System.out.println(Lang.t("Неверный выбор.", "Invalid choice.", "Жарамсыз таңдау."));
        }
    }

    private static void handleApproveRegistration(Manager manager, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Одобрение регистрации", "Approve Registration", "Тіркелуді растау") + " ---");
        List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u).toList();
        if (students.isEmpty()) {
            System.out.println(Lang.t("Нет студентов.", "No students.", "Студенттер жоқ."));
            return;
        }
        students.forEach(s -> System.out.printf("  %-15s | %s%n", s.getLogin(), s.getFullName()));
        System.out.print(Lang.t("Логин студента: ", "Student login: ", "Студент логині: "));
        String sLogin = scanner.nextLine().trim();
        Student student = students.stream()
                .filter(s -> s.getLogin().equalsIgnoreCase(sLogin)).findFirst().orElse(null);
        if (student == null) {
            System.out.println(Lang.t("❌ Не найден.", "❌ Not found.", "❌ Табылмады."));
            return;
        }
        university.getCourses().forEach(c -> System.out.println("  " + c));
        System.out.print(Lang.t("Код курса: ", "Course code: ", "Курс коды: "));
        String code = scanner.nextLine().trim();
        Course course = university.getCourses().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
        if (course == null) {
            System.out.println(Lang.t("❌ Курс не найден.", "❌ Course not found.", "❌ Курс табылмады."));
            return;
        }
        manager.approveRegistration(student, course);
    }

    private static void handleManageNews(Manager manager, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Управление новостями", "Manage News", "Жаңалықтарды басқару") + " ---");
        System.out.println("1. " + Lang.t("Добавить новость", "Add News", "Жаңалық қосу"));
        System.out.println("2. " + Lang.t("Просмотреть все", "View All", "Барлығын көру"));
        System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));
        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) {
            System.out.print(Lang.t("Заголовок: ", "Title: ", "Тақырып: "));
            String title = scanner.nextLine().trim();
            System.out.print(Lang.t("Текст: ", "Content: ", "Мәтін: "));
            String content = scanner.nextLine().trim();
            System.out.print(Lang.t("Топик (RESEARCH/GENERAL): ", "Topic (RESEARCH/GENERAL): ", "Тақырып (RESEARCH/GENERAL): "));
            String topic = scanner.nextLine().trim();
            university.addNews(new News(title, content, topic));
            System.out.println(Lang.t("✅ Новость добавлена.", "✅ News added.", "✅ Жаңалық қосылды."));
        } else if (choice.equals("2")) {
            List<News> newsList = new java.util.ArrayList<>(university.getNews());
            java.util.Collections.sort(newsList);
            if (newsList.isEmpty()) {
                System.out.println(Lang.t("Новостей нет.", "No news.", "Жаңалықтар жоқ."));
                return;
            }
            newsList.forEach(System.out::println);
        }
    }

    private static void handleAssignTeacher(Manager manager, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Назначение преподавателя", "Assign Teacher", "Оқытушыны тағайындау") + " ---");
        List<Teacher> teachers = university.getUsers().stream()
                .filter(u -> u instanceof Teacher).map(u -> (Teacher) u).toList();
        if (teachers.isEmpty()) {
            System.out.println(Lang.t("Нет преподавателей.", "No teachers.", "Оқытушылар жоқ."));
            return;
        }
        teachers.forEach(t -> System.out.printf("  %-15s | %s | %s%n",
                t.getLogin(), t.getFullName(), t.getPosition()));
        university.getCourses().forEach(c -> System.out.println("  " + c));
        System.out.print(Lang.t("Логин преподавателя: ", "Teacher login: ", "Оқытушы логині: "));
        String tLogin = scanner.nextLine().trim();
        Teacher teacher = teachers.stream()
                .filter(t -> t.getLogin().equalsIgnoreCase(tLogin)).findFirst().orElse(null);
        if (teacher == null) {
            System.out.println(Lang.t("❌ Не найден.", "❌ Not found.", "❌ Табылмады."));
            return;
        }
        System.out.print(Lang.t("Код курса: ", "Course code: ", "Курс коды: "));
        String code = scanner.nextLine().trim();
        Course course = university.getCourses().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
        if (course == null) {
            System.out.println(Lang.t("❌ Курс не найден.", "❌ Course not found.", "❌ Курс табылмады."));
            return;
        }
        System.out.println(Lang.t("Тип: 1. LECTURE  2. PRACTICE", "Type: 1. LECTURE  2. PRACTICE", "Түрі: 1. LECTURE  2. PRACTICE"));
        System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));
        LessonType lessonType = scanner.nextLine().trim().equals("2") ? LessonType.PRACTICE : LessonType.LECTURE;
        course.addTeacher(teacher, lessonType);
        teacher.addCourse(course);
        System.out.printf(Lang.t("✅ %s назначен на %s (%s)%n", "✅ %s assigned to %s (%s)%n", "✅ %s тағайындалды %s (%s)%n"),
                teacher.getFullName(), course.getName(), lessonType);
    }
}