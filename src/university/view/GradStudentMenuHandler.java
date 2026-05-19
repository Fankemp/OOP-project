package university.view;

import university.storage.University;
import university.academic.Course;
import university.users.GraduateStudent;
import university.users.Teacher;
import university.exceptions.MaxCreditsException;
import university.exceptions.CourseFailLimitException;
import university.exceptions.LowHIndexException;

import java.util.List;
import java.util.Scanner;

public class GradStudentMenuHandler {
    private static final University university = University.getInstance();

    public static boolean show(GraduateStudent student, Scanner scanner) {
        System.out.println("\n--- [" + Lang.t("Панель Магистранта/PhD", "Graduate Student Panel", "Магистрант/PhD панелі") + "] ---");
        System.out.println("1. "  + Lang.t("Зарегистрироваться на курс", "Register for Course", "Курсқа тіркелу"));
        System.out.println("2. "  + Lang.t("Посмотреть оценки", "View Marks", "Бағаларды көру"));
        System.out.println("3. "  + Lang.t("Транскрипт и GPA", "Transcript and GPA", "Транскрипт және GPA"));
        System.out.println("4. "  + Lang.t("Отправить сообщение", "Send Message", "Хабар жіберу"));
        System.out.println("5. "  + Lang.t("Почтовый ящик", "Mailbox", "Пошта жәшігі"));
        System.out.println("6. "  + Lang.t("Оценить преподавателя", "Rate Teacher", "Оқытушыны бағалау"));
        System.out.println("7. "  + Lang.t("Меню исследователя", "Research Menu", "Зерттеуші мәзірі"));
        System.out.println("8. "  + Lang.t("Назначить руководителя", "Set Supervisor", "Жетекшіні тағайындау"));
        System.out.println("9. "  + Lang.t("Выйти", "Logout", "Шығу"));
        System.out.println("10. " + Lang.t("Заявка в техподдержку", "Tech Request", "Техқолдауға өтініш"));
        System.out.print(Lang.t("Выберите действие: ", "Choose action: ", "Әрекетті таңдаңыз: "));

        switch (scanner.nextLine().trim()) {
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
                } catch (MaxCreditsException | CourseFailLimitException e) {
                    System.out.println(Lang.t("❌ Отклонено: ", "❌ Rejected: ", "❌ Қабылданмады: ") + e.getMessage());
                }
            }
            case "2" -> student.viewMarks();
            case "3" -> System.out.println(student.getTranscript().toString());
            case "4" -> CommunicationHandler.handleSendMessageMenu(student, scanner);
            case "5" -> CommunicationHandler.handleViewMailboxMenu(student, scanner);
            case "6" -> handleRateTeacher(student, scanner);
            case "7" -> TeacherMenuHandler.handleResearcherMenu(student, scanner);
            case "8" -> handleSetSupervisor(student, scanner);
            case "9" -> {
                System.out.println(Lang.t("Сессия завершена.", "Session ended.", "Сессия аяқталды."));
                return true;
            }
            case "10" -> TechSupportMenuHandler.handleCreateRequest(student, scanner);
            default -> System.out.println(Lang.t("❌ Неверный выбор.", "❌ Invalid choice.", "❌ Жарамсыз таңдау."));
        }
        return false;
    }

    private static void handleSetSupervisor(GraduateStudent student, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Назначение руководителя", "Set Supervisor", "Жетекшіні тағайындау") + " ---");
        List<Teacher> teachers = university.getUsers().stream()
                .filter(u -> u instanceof Teacher).map(u -> (Teacher) u).toList();
        if (teachers.isEmpty()) {
            System.out.println(Lang.t("Нет преподавателей.", "No teachers.", "Оқытушылар жоқ."));
            return;
        }
        teachers.forEach(t -> System.out.printf("  %-15s | %-25s | h-index: %d%n",
                t.getLogin(), t.getFullName(), t.calculateHIndex()));
        System.out.print(Lang.t("Логин руководителя: ", "Supervisor login: ", "Жетекші логині: "));
        String tLogin = scanner.nextLine().trim();
        Teacher teacher = teachers.stream()
                .filter(t -> t.getLogin().equalsIgnoreCase(tLogin)).findFirst().orElse(null);
        if (teacher == null) {
            System.out.println(Lang.t("❌ Не найден.", "❌ Not found.", "❌ Табылмады."));
            return;
        }
        try {
            student.setSupervisor(teacher);
            System.out.println(Lang.t("✅ Руководитель назначен: ", "✅ Supervisor set: ", "✅ Жетекші тағайындалды: ") + teacher.getFullName());
        } catch (LowHIndexException e) {
            System.out.println(Lang.t("❌ Ошибка: ", "❌ Error: ", "❌ Қате: ") + e.getMessage());
        }
    }

    private static void handleRateTeacher(GraduateStudent student, Scanner scanner) {
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