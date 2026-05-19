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

/**
 * Изолированный обработчик интерфейса для магистрантов и докторантов (GraduateStudent).
 * Объединяет последипломную академическую и научно-исследовательскую деятельность.
 */
public class GradStudentMenuHandler {
    private static final University university = University.getInstance();

    /**
     * Отображает меню последипломного студента и обрабатывает его выбор.
     * @return true если пользователь выбрал Logout, иначе false.
     */
    public static boolean show(GraduateStudent student, Scanner scanner) {
        System.out.println("\n--- [Панель Магистранта / PhD Докторанта KBTU] ---");
        System.out.println("1. Зарегистрироваться на учебный курс");
        System.out.println("2. Посмотреть текущие оценки (View Marks)");
        System.out.println("3. Распечатать академический транскрипт и GPA");
        System.out.println("4. Отправить личное сообщение (Send Message)");
        System.out.println("5. Посмотреть мой почтовый ящик (Mailbox)");
        System.out.println("6. Оценить преподавателя (Rate Teacher)");
        System.out.println("7. Исследовательское меню (Research Menu)");
        System.out.println("8. Назначить научного руководителя (Set Supervisor)");
        System.out.println("9. Выйти из системы (Logout)");
        System.out.println("10. Отправить заявку в техподдержку (Send Tech Request)");
        System.out.print("Выберите действие: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.println("\n--- Доступные курсы для регистрации ---");
                university.getCourses().forEach(System.out::println);
                System.out.print("Введите код курса: ");
                String code = scanner.nextLine().trim();
                Course course = university.getCourses().stream()
                        .filter(c -> c.getCode().equalsIgnoreCase(code))
                        .findFirst().orElse(null);

                if (course == null) {
                    System.out.println("❌ Ошибка: Курс не найден.");
                    return false;
                }
                try {
                    student.registerCourse(course);
                    System.out.println("✅ Регистрация на курс прошла успешно.");
                } catch (MaxCreditsException | CourseFailLimitException e) {
                    System.out.println("❌ Регистрация отклонена: " + e.getMessage());
                }
            }
            case "2" -> student.viewMarks();
            case "3" -> System.out.println(student.getTranscript().toString());

            // Использование сквозных функций CommunicationHandler
            case "4" -> CommunicationHandler.handleSendMessageMenu(student, scanner);
            case "5" -> CommunicationHandler.handleViewMailboxMenu(student, scanner);

            case "6" -> handleRateTeacher(student, scanner);

            // Вызов публичного исследовательского меню из TeacherMenuHandler
            case "7" -> TeacherMenuHandler.handleResearcherMenu(student, scanner);

            case "8" -> handleSetSupervisor(student, scanner);
            case "9" -> {
                System.out.println("Сессия последипломного обучения завершена.");
                return true; // Сигнал логаута для ConsoleInterface
            }
            case "10" -> TechSupportMenuHandler.handleCreateRequest(student, scanner);
            default -> System.out.println("Ошибка: Неверный выбор пункта меню.");
        }
        return false;
    }

    private static void handleSetSupervisor(GraduateStudent student, Scanner scanner) {
        System.out.println("\n--- Назначение научного руководителя (Research Supervisor) ---");
        List<Teacher> teachers = university.getUsers().stream()
                .filter(u -> u instanceof Teacher)
                .map(u -> (Teacher) u)
                .toList();

        if (teachers.isEmpty()) {
            System.out.println("В системе пока нет зарегистрированных преподавателей.");
            return;
        }

        // Вывод списка потенциальных руководителей и их h-index
        teachers.forEach(t -> System.out.printf("  Логин: %-15s | ФИО: %-25s | Индекс Хирша (h-index): %d%n",
                t.getLogin(), t.getFullName(), t.calculateHIndex()));

        System.out.print("\nВведите логин выбранного руководителя: ");
        String tLogin = scanner.nextLine().trim();
        Teacher teacher = teachers.stream()
                .filter(t -> t.getLogin().equalsIgnoreCase(tLogin))
                .findFirst().orElse(null);

        if (teacher == null) {
            System.out.println("❌ Ошибка: Преподаватель с таким логином не найден.");
            return;
        }

        try {
            // Если у руководителя h-index < 3, сработает LowHIndexException
            student.setSupervisor(teacher);
            System.out.println("✅ Научный руководитель успешно назначен: " + teacher.getFullName());
        } catch (LowHIndexException e) {
            System.out.println("❌ Запрещено системой: " + e.getMessage());
        }
    }

    private static void handleRateTeacher(GraduateStudent student, Scanner scanner) {
        System.out.println("\n--- Анкетирование: Оценка преподавателей ---");
        List<Teacher> teachers = university.getUsers().stream()
                .filter(u -> u instanceof Teacher).map(u -> (Teacher) u).toList();
        if (teachers.isEmpty()) {
            System.out.println("В системе нет преподавателей для оценки.");
            return;
        }

        teachers.forEach(t -> System.out.printf("  Логин: %-15s | ФИО: %s | Рейтинг: %.1f%n",
                t.getLogin(), t.getFullName(), t.getRating()));

        System.out.print("\nВведите логин преподавателя: ");
        String tLogin = scanner.nextLine().trim();
        Teacher teacher = teachers.stream()
                .filter(t -> t.getLogin().equalsIgnoreCase(tLogin)).findFirst().orElse(null);
        if (teacher == null) {
            System.out.println("❌ Ошибка: Преподаватель не найден.");
            return;
        }

        System.out.print("Выставите оценку (1-10): ");
        try {
            int rating = Integer.parseInt(scanner.nextLine().trim());
            if (rating < 1 || rating > 10) {
                System.out.println("❌ Ошибка: Оценка должна быть строго в диапазоне от 1 до 10.");
                return;
            }
            student.rateTeacher(teacher, rating);
            System.out.printf("🎉 Оценка принята. Новый средний рейтинг %s: %.1f%n",
                    teacher.getFullName(), teacher.getRating());
        } catch (NumberFormatException e) {
            System.out.println("❌ Ошибка: Введите корректное целое число.");
        }
    }
}