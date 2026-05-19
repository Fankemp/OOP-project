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

import java.util.List;
import java.util.Scanner;

/**
 * Изолированный обработчик интерфейса для Личного кабинета Студента.
 * Кодировка полностью восстановлена, интегрирован сквозной вызов службы техподдержки.
 */
public class StudentMenuHandler {
    private static final University university = University.getInstance();

    public static boolean show(Student student, Scanner scanner) {
        System.out.println("\n--- [Личный кабинет Студента KBTU] ---");
        System.out.println("1. Зарегистрироваться на учебный курс");
        System.out.println("2. Посмотреть текущие оценки (View Marks)");
        System.out.println("3. Распечатать академический транскрипт и GPA");
        System.out.println("4. Отправить личное сообщение (Send Message)");
        System.out.println("5. Посмотреть мой почтовый ящик (Mailbox)");
        System.out.println("6. Оценить преподавателя (Rate Teacher)");
        System.out.println("7. Управление студенческими организациями");
        System.out.println("8. Подписка на университетские журналы");
        System.out.println("9. Сменить язык системы (Switch Language)");
        System.out.println("10. Выйти из системы (Logout)");
        System.out.println("11. Отправить заявку в техподдержку (Send Tech Request)");
        System.out.println("12. Посмотреть ленту новостей KBTU (View News)"); // НАШ НОВЫЙ ПУНКТ!
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.println("\n--- Доступный каталог курсов для регистрации ---");
                university.getCourses().forEach(System.out::println);
                System.out.print("Введите код выбранного курса: ");
                String code = scanner.nextLine().trim();
                Course course = university.getCourses().stream()
                        .filter(c -> c.getCode().equalsIgnoreCase(code))
                        .findFirst().orElse(null);

                if (course == null) {
                    System.out.println("❌ Ошибка: Указанный курс не найден в каталоге.");
                    return false;
                }
                try {
                    student.registerCourse(course);
                    System.out.println("✅ Регистрация на дисциплину успешно завершена.");
                } catch (MaxCreditsException | CourseFailLimitException ex) {
                    System.out.println("❌ Регистрация отклонена системой: " + ex.getMessage());
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
                System.out.println("Сессия работы студента успешно завершена.");
                return true;
            }
            case "11" -> TechSupportMenuHandler.handleCreateRequest(student, scanner);

            // ВЫЗОВ НОВОГО МЕТОДА ИЗ ОБЩИХ КОМПОНЕНТОВ:
            case "12" -> SharedMenuComponents.handleViewNews();

            default -> System.out.println("⚠️ Ошибка: Неверный выбор пункта меню.");
        }
        return false;
    }

    private static void handleJournalSubscription(User user, Scanner scanner) {
        System.out.println("\n--- Научные журналы университета KBTU ---");
        List<UniversityJournal> journals = university.getJournals();
        if (journals.isEmpty()) { System.out.println("В каталоге пока нет доступных журналов."); return; }

        journals.forEach(j -> System.out.printf("  ID: %-8s | Название: %-25s | ISSN: %s | Статей: %d%n",
                j.getJournalId(), j.getName(), j.getIssn(), j.getPapers().size()));

        System.out.println("\n1. Подписаться на журнал");
        System.out.println("2. Вернуться назад");
        System.out.print("Ваш выбор: ");

        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.print("Введите ID выбранного журнала: ");
        String jid = scanner.nextLine().trim();
        UniversityJournal journal = journals.stream()
                .filter(j -> j.getJournalId().equalsIgnoreCase(jid))
                .findFirst().orElse(null);
        if (journal == null) { System.out.println("❌ Ошибка: Журнал с таким ID не найден."); return; }

        // Паттерн Observer (Наблюдатель) в действии
        journal.subscribe((paper, j) -> System.out.printf(
                "%n🔔 [УВЕДОМЛЕНИЕ ДЛЯ ПОДПИСЧИКА %s]: В журнале '%s' опубликована новая статья: \"%s\"%n",
                user.getFullName(), j.getName(), paper.getTitle()));

        System.out.println("✅ Вы успешно подписались на обновления журнала: " + journal.getName());
    }

    private static void handleStudentOrganizations(Student student, Scanner scanner) {
        System.out.println("\n--- Студенческие организации и клубы ---");
        System.out.println("1. Просмотреть список всех организаций");
        System.out.println("2. Вступить в существующую организацию");
        System.out.println("3. Основать новую организацию (Стать главой)");
        System.out.println("4. Покинуть организацию");
        System.out.print("Выберите действие: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                List<StudentOrganization> orgs = university.getOrganizations();
                if (orgs.isEmpty()) { System.out.println("На текущий момент клубы не зарегистрированы."); return; }
                orgs.forEach(System.out::println);
            }
            case "2" -> {
                List<StudentOrganization> orgs = university.getOrganizations();
                if (orgs.isEmpty()) { System.out.println("Нет доступных организаций для вступления."); return; }
                orgs.forEach(o -> System.out.printf("  %d. %s%n", orgs.indexOf(o) + 1, o.getName()));

                System.out.print("Введите точное название организации: ");
                String name = scanner.nextLine().trim();
                StudentOrganization org = orgs.stream()
                        .filter(o -> o.getName().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
                if (org == null) { System.out.println("❌ Ошибка: Клуб не найден."); return; }
                org.addMember(student);
                System.out.println("✅ Вы успешно зачислены в состав участников " + org.getName());
            }
            case "3" -> {
                System.out.print("Введите название новой организации: ");
                String name = scanner.nextLine().trim();
                if (name.isEmpty()) { System.out.println("❌ Ошибка: Название не может быть пустым."); return; }

                StudentOrganization org = new StudentOrganization(name, student);
                university.addOrganization(org);
                System.out.println("✅ Организация '" + name + "' создана. Вы назначены её руководителем (Head).");
            }
            case "4" -> {
                List<StudentOrganization> orgs = university.getOrganizations();
                if (orgs.isEmpty()) { System.out.println("Вы не состоите ни в одной организации."); return; }
                System.out.print("Введите название организации, которую хотите покинуть: ");
                String name = scanner.nextLine().trim();
                StudentOrganization org = orgs.stream()
                        .filter(o -> o.getName().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
                if (org == null) { System.out.println("❌ Ошибка: Клуб не найден."); return; }
                org.removeMember(student);
                System.out.println("🚪 Вы успешно вышли из состава организации.");
            }
            default -> System.out.println("Неверный выбор.");
        }
    }

    private static void handleSwitchLanguage(User user, Scanner scanner) {
        System.out.println("\n--- Настройка локализации / Switch Language ---");
        System.out.println("1. English (EN)\n2. Казахский (KZ)\n3. Русский (RU)");
        System.out.print("Ваш выбор: ");
        String choice = scanner.nextLine().trim();
        String lang = switch (choice) {
            case "2" -> "KZ";
            case "3" -> "RU";
            default  -> "EN";
        };
        user.switchLanguage(lang);
        System.out.println("Language successfully switched to: " + lang);
    }

    private static void handleRateTeacher(Student student, Scanner scanner) {
        System.out.println("\n--- Анкетирование: Оценка преподавателей ---");
        List<Teacher> teachers = university.getUsers().stream()
                .filter(u -> u instanceof Teacher).map(u -> (Teacher) u).toList();
        if (teachers.isEmpty()) { System.out.println("В системе нет преподавателей для оценки."); return; }

        teachers.forEach(t -> System.out.printf("  Логин: %-15s | ФИО: %-25s | Текущий рейтинг: %.1f%n",
                t.getLogin(), t.getFullName(), t.getRating()));

        System.out.print("\nВведите логин преподавателя: ");
        String tLogin = scanner.nextLine().trim();
        Teacher teacher = teachers.stream()
                .filter(t -> t.getLogin().equalsIgnoreCase(tLogin))
                .findFirst().orElse(null);
        if (teacher == null) { System.out.println("❌ Ошибка: Преподаватель с таким логином не найден."); return; }

        System.out.print("Выставите оценку качеству преподавания (от 1 до 10): ");
        try {
            int rating = Integer.parseInt(scanner.nextLine().trim());
            if (rating < 1 || rating > 10) {
                System.out.println("❌ Ошибка: Рейтинг должен быть строго в диапазоне от 1 до 10.");
                return;
            }
            student.rateTeacher(teacher, rating);
            System.out.printf("🎉 Спасибо за участие! Новый средний рейтинг преподавателя %s: %.1f%n",
                    teacher.getFullName(), teacher.getRating());
        } catch (NumberFormatException e) {
            System.out.println("❌ Ошибка: Введите корректное целое число.");
        }
    }
}