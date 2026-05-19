package university.view;

import university.storage.University;
import university.academic.Course;
import university.users.Student;
import university.users.Teacher;
import university.users.User;
import university.users.StudentOrganization;
import university.exceptions.MaxCreditsException;
import university.exceptions.CourseFailLimitException;
import university.research.journal.UniversityJournal;
import university.enums.Language;

import java.util.List;
import java.util.Scanner;

public class StudentMenuHandler {
    private static final University university = University.getInstance();

    public static boolean show(Student student, Scanner scanner) {
        System.out.println("\n--- [" + Lang.t("Личный кабинет Студента", "Student Panel", "Студент кабинеті") + "] ---");
        System.out.println("1. " + Lang.t("Зарегистрироваться на курс", "Register for Course", "Курсқа тіркелу"));
        System.out.println("2. " + Lang.t("Посмотреть оценки", "View Marks", "Бағаларды көру"));
        System.out.println("3. " + Lang.t("Транскрипт и GPA", "Transcript and GPA", "Транскрипт және GPA"));
        System.out.println("4. " + Lang.t("Отправить сообщение", "Send Message", "Хабар жіберу"));
        System.out.println("5. " + Lang.t("Почтовый ящик", "Mailbox", "Пошта жәшігі"));
        System.out.println("6. " + Lang.t("Оценить преподавателя", "Rate Teacher", "Оқытушыны бағалау"));
        System.out.println("7. " + Lang.t("Студенческие организации", "Student Organizations", "Студенттік ұйымдар"));
        System.out.println("8. " + Lang.t("Подписка на журналы", "Journal Subscription", "Журналға жазылу"));
        System.out.println("9. " + Lang.t("Сменить язык", "Switch Language", "Тілді ауыстыру"));
        System.out.println("10. " + Lang.t("Выйти", "Logout", "Шығу"));
        System.out.println("11. " + Lang.t("Заявка в техподдержку", "Tech Support Request", "Техқолдауға өтініш"));
        System.out.println("12. " + Lang.t("Лента новостей", "View News", "Жаңалықтар"));
        System.out.print(Lang.t("Выберите действие: ", "Choose action: ", "Әрекетті таңдаңыз: "));

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.println("\n--- " + Lang.t("Каталог курсов", "Course Catalog", "Курстар каталогы") + " ---");
                university.getCourses().forEach(System.out::println);
                System.out.print(Lang.t("Код курса: ", "Course code: ", "Курс коды: "));
                String code = scanner.nextLine().trim();
                Course course = university.getCourses().stream()
                        .filter(c -> c.getCode().equalsIgnoreCase(code))
                        .findFirst().orElse(null);
                if (course == null) {
                    System.out.println(Lang.t("❌ Курс не найден.", "❌ Course not found.", "❌ Курс табылмады."));
                    return false;
                }
                try {
                    student.registerCourse(course);
                    System.out.println(Lang.t("✅ Регистрация завершена.", "✅ Registered.", "✅ Тіркелу аяқталды."));
                } catch (MaxCreditsException | CourseFailLimitException ex) {
                    System.out.println(Lang.t("❌ Отклонено: ", "❌ Rejected: ", "❌ Қабылданбады: ") + ex.getMessage());
                }
            }
            case "2" -> student.viewMarks();
            case "3" -> System.out.println(student.getTranscript().toString());
            case "4" -> CommunicationHandler.handleSendMessageMenu(student, scanner);
            case "5" -> CommunicationHandler.handleViewMailboxMenu(student, scanner);
            case "6" -> handleRateTeacher(student, scanner);
            case "7" -> handleStudentOrganizations(student, scanner);
            case "8" -> handleJournalSubscription(student, scanner);
            case "9" -> handleSwitchLanguage(student, scanner);
            case "10" -> {
                System.out.println(Lang.t("Сессия завершена.", "Session ended.", "Сессия аяқталды."));
                return true;
            }
            case "11" -> TechSupportMenuHandler.handleCreateRequest(student, scanner);
            case "12" -> SharedMenuComponents.handleViewNews();
            default -> System.out.println(Lang.t("⚠️ Неверный выбор.", "⚠️ Invalid choice.", "⚠️ Жарамсыз таңдау."));
        }
        return false;
    }

    private static void handleJournalSubscription(User user, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Журналы университета", "University Journals", "Университет журналдары") + " ---");
        List<UniversityJournal> journals = university.getJournals();
        if (journals.isEmpty()) {
            System.out.println(Lang.t("Журналов нет.", "No journals.", "Журналдар жоқ."));
            return;
        }
        journals.forEach(j -> System.out.printf("  ID: %-8s | %-25s | ISSN: %s%n",
                j.getJournalId(), j.getName(), j.getIssn()));
        System.out.println("1. " + Lang.t("Подписаться", "Subscribe", "Жазылу"));
        System.out.println("2. " + Lang.t("Назад", "Back", "Артқа"));
        System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));
        if (!scanner.nextLine().trim().equals("1")) return;
        System.out.print(Lang.t("ID журнала: ", "Journal ID: ", "Журнал ID: "));
        String jid = scanner.nextLine().trim();
        UniversityJournal journal = journals.stream()
                .filter(j -> j.getJournalId().equalsIgnoreCase(jid))
                .findFirst().orElse(null);
        if (journal == null) {
            System.out.println(Lang.t("❌ Не найден.", "❌ Not found.", "❌ Табылмады."));
            return;
        }
        journal.subscribe((paper, j) -> System.out.printf(
                "%n🔔 %s: %s \"%s\"%n", user.getFullName(), j.getName(), paper.getTitle()));
        System.out.println(Lang.t("✅ Подписка оформлена: ", "✅ Subscribed: ", "✅ Жазылды: ") + journal.getName());
    }

    private static void handleStudentOrganizations(Student student, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Студенческие организации", "Student Organizations", "Студенттік ұйымдар") + " ---");
        System.out.println("1. " + Lang.t("Просмотреть все", "View All", "Барлығын көру"));
        System.out.println("2. " + Lang.t("Вступить", "Join", "Кіру"));
        System.out.println("3. " + Lang.t("Создать", "Create", "Жасау"));
        System.out.println("4. " + Lang.t("Покинуть", "Leave", "Шығу"));
        System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                List<StudentOrganization> orgs = university.getOrganizations();
                if (orgs.isEmpty()) {
                    System.out.println(Lang.t("Организаций нет.", "No organizations.", "Ұйымдар жоқ."));
                    return;
                }
                orgs.forEach(System.out::println);
            }
            case "2" -> {
                List<StudentOrganization> orgs = university.getOrganizations();
                if (orgs.isEmpty()) {
                    System.out.println(Lang.t("Нет организаций.", "No organizations.", "Ұйымдар жоқ."));
                    return;
                }
                orgs.forEach(o -> System.out.printf("  %d. %s%n", orgs.indexOf(o) + 1, o.getName()));
                System.out.print(Lang.t("Название: ", "Name: ", "Атауы: "));
                String name = scanner.nextLine().trim();
                StudentOrganization org = orgs.stream()
                        .filter(o -> o.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
                if (org == null) {
                    System.out.println(Lang.t("❌ Не найдена.", "❌ Not found.", "❌ Табылмады."));
                    return;
                }
                org.addMember(student);
            }
            case "3" -> {
                System.out.print(Lang.t("Название организации: ", "Organization name: ", "Ұйым атауы: "));
                String name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    System.out.println(Lang.t("❌ Название пустое.", "❌ Name is empty.", "❌ Атау бос."));
                    return;
                }
                university.addOrganization(new StudentOrganization(name, student));
                System.out.println(Lang.t("✅ Создана: ", "✅ Created: ", "✅ Жасалды: ") + name);
            }
            case "4" -> {
                List<StudentOrganization> orgs = university.getOrganizations();
                if (orgs.isEmpty()) {
                    System.out.println(Lang.t("Нет организаций.", "No organizations.", "Ұйымдар жоқ."));
                    return;
                }
                System.out.print(Lang.t("Название: ", "Name: ", "Атауы: "));
                String name = scanner.nextLine().trim();
                StudentOrganization org = orgs.stream()
                        .filter(o -> o.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
                if (org == null) {
                    System.out.println(Lang.t("❌ Не найдена.", "❌ Not found.", "❌ Табылмады."));
                    return;
                }
                org.removeMember(student);
            }
            default -> System.out.println(Lang.t("Неверный выбор.", "Invalid choice.", "Жарамсыз таңдау."));
        }
    }

    private static void handleSwitchLanguage(User user, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Смена языка", "Switch Language", "Тілді ауыстыру") + " ---");
        System.out.println("1. Русский (RU)");
        System.out.println("2. English (EN)");
        System.out.println("3. Қазақша (KZ)");
        System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));
        Language lang = switch (scanner.nextLine().trim()) {
            case "2" -> Language.ENG;
            case "3" -> Language.KZ;
            default  -> Language.RU;
        };
        Lang.set(lang);
        user.switchLanguage(lang.name());
        System.out.println(Lang.t("✅ Язык изменён.", "✅ Language changed.", "✅ Тіл өзгертілді."));
    }

    private static void handleRateTeacher(Student student, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Оценка преподавателя", "Rate Teacher", "Оқытушыны бағалау") + " ---");
        List<Teacher> teachers = university.getUsers().stream()
                .filter(u -> u instanceof Teacher).map(u -> (Teacher) u).toList();
        if (teachers.isEmpty()) {
            System.out.println(Lang.t("Нет преподавателей.", "No teachers.", "Оқытушылар жоқ."));
            return;
        }
        teachers.forEach(t -> System.out.printf("  %-15s | %-25s | %.1f%n",
                t.getLogin(), t.getFullName(), t.getRating()));
        System.out.print(Lang.t("Логин преподавателя: ", "Teacher login: ", "Оқытушы логині: "));
        String tLogin = scanner.nextLine().trim();
        Teacher teacher = teachers.stream()
                .filter(t -> t.getLogin().equalsIgnoreCase(tLogin)).findFirst().orElse(null);
        if (teacher == null) {
            System.out.println(Lang.t("❌ Не найден.", "❌ Not found.", "❌ Табылмады."));
            return;
        }
        System.out.print(Lang.t("Оценка (1-10): ", "Rating (1-10): ", "Баға (1-10): "));
        try {
            int rating = Integer.parseInt(scanner.nextLine().trim());
            if (rating < 1 || rating > 10) {
                System.out.println(Lang.t("❌ Диапазон 1-10.", "❌ Range 1-10.", "❌ 1-10 аралығы."));
                return;
            }
            student.rateTeacher(teacher, rating);
            System.out.printf(Lang.t("✅ Рейтинг %s: %.1f%n", "✅ Rating %s: %.1f%n", "✅ Рейтинг %s: %.1f%n"),
                    teacher.getFullName(), teacher.getRating());
        } catch (NumberFormatException e) {
            System.out.println(Lang.t("❌ Введите число.", "❌ Enter a number.", "❌ Сан енгізіңіз."));
        }
    }
}