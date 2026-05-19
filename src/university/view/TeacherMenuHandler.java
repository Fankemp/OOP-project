package university.view;

import university.storage.University;
import university.academic.Course;
import university.academic.Mark;
import university.enums.UrgencyLevel;
import university.users.Teacher;
import university.users.Student;
import university.users.Manager;
import university.users.User;
import university.research.Researcher;
import university.research.ResearchManager;
import university.research.ResearchPaper;
import university.research.ResearchProject;
import university.communications.News;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Изолированный обработчик интерфейса для роли Преподавателя (Teacher) и Исследователя (Researcher).
 * Кодировка полностью восстановлена, методы разграничены по правам доступа.
 */
public class TeacherMenuHandler {
    private static final University university = University.getInstance();

    /**
     * Отображает главное меню преподавателя.
     * @return true если пользователь выбрал Logout, иначе false.
     */
    public static boolean show(Teacher teacher, Scanner scanner) {
        System.out.println("\n--- [Панель Преподавателя KBTU] ---");
        System.out.println("1. Просмотреть учебные дисциплины");
        System.out.println("2. Посмотреть список студентов на моих курсах");
        System.out.println("3. Отправить личное сообщение (Send Message)");
        System.out.println("4. Проверить почтовый ящик (Mailbox)");
        System.out.println("5. Выставить оценки студентам (Put Mark)");
        System.out.println("6. Отправить жалобу в деканат (Send Complaint)");
        System.out.println("7. Меню исследователя (Research Panel)");
        System.out.println("8. Выйти из системы (Logout)");
        System.out.print("Выберите действие: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> teacher.viewCourses();
            case "2" -> teacher.viewStudents();

            // Переиспользование сквозного CommunicationHandler для экономии строк кода (DRY)
            case "3" -> CommunicationHandler.handleSendMessageMenu(teacher, scanner);
            case "4" -> CommunicationHandler.handleViewMailboxMenu(teacher, scanner);

            case "5" -> handlePutMark(teacher, scanner);
            case "6" -> handleSendComplaint(teacher, scanner);
            case "7" -> handleResearcherMenu(teacher, scanner);
            case "8" -> {
                System.out.println("Сессия преподавателя завершена.");
                return true; // Сигнал логаута для диспетчера ConsoleInterface
            }
            default -> System.out.println("Ошибка: Неверный выбор пункта меню.");
        }
        return false;
    }

    /**
     * ПУБЛИЧНЫЙ МЕТОД: Отвечает за меню исследовательской подсистемы KBTU.
     * Открыт для вызова из GradStudentMenuHandler.
     */
    public static void handleResearcherMenu(Researcher researcher, Scanner scanner) {
        boolean inResearchMenu = true;
        while (inResearchMenu) {
            System.out.println("\n--- [Меню Исследователя KBTU] ---");
            System.out.println("1. Просмотреть мои научные статьи");
            System.out.println("2. Опубликовать новую статью (Add Paper)");
            System.out.println("3. Рассчитать мой h-index");
            System.out.println("4. Печать моих статей (Сортировка по цитированиям)");
            System.out.println("5. Просмотреть все статьи университета");
            System.out.println("6. Найти топ-цитируемого ученого конкретного года");
            System.out.println("7. Инициировать научный проект (Research Project)");
            System.out.println("8. Список моих научных проектов");
            System.out.println("9. Вернуться назад");
            System.out.print("Выберите действие: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    List<ResearchPaper> papers = researcher.getPapers();
                    if (papers.isEmpty()) { System.out.println("У вас пока нет опубликованных работ."); return; }
                    papers.forEach(System.out::println);
                }
                case "2" -> {
                    try {
                        System.out.print("Title: "); String title = scanner.nextLine().trim();
                        System.out.print("Authors (comma separated): ");
                        List<String> authors = Arrays.asList(scanner.nextLine().split(","));
                        System.out.print("Journal: "); String journal = scanner.nextLine().trim();
                        System.out.print("Start page: "); int start = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("End page: "); int end = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Year (e.g. 2026): "); int year = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("DOI: "); String doi = scanner.nextLine().trim();
                        System.out.print("Citations: "); int citations = Integer.parseInt(scanner.nextLine().trim());

                        ResearchPaper paper = new ResearchPaper(title, authors, journal, start, end,
                                LocalDate.of(year, 1, 1), doi, citations);
                        ResearchManager.getInstance().registerResearcher(researcher);
                        researcher.addPaper(paper);
                        System.out.println("✅ Научная статья успешно зарегистрирована в системе.");

                        // Авто-генерация новости о публикации статьи
                        String authorName = (researcher instanceof User) ? ((User) researcher).getFullName() : "Researcher";
                        News paperNews = new News(
                                "New Research Paper Published",
                                authorName + " published: \"" + title + "\" in " + journal,
                                "RESEARCH"
                        );
                        university.addNews(paperNews);
                        System.out.println("Системный анонс сгенерирован автоматически.");

                        // Авто-обновление новости про топ-ученого университета
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
                        System.out.println("❌ Ошибка ввода: Страницы, год и цитирования должны быть числами.");
                    }
                }
                case "3" -> System.out.println("Ваш текущий индекс Хирша (h-index): " + researcher.calculateHIndex());
                case "4" -> {
                    java.util.Comparator<ResearchPaper> byCitations =
                            (p1, p2) -> Integer.compare(p2.getCitations(), p1.getCitations());
                    researcher.printPapers(byCitations);
                }
                case "5" -> {
                    ResearchManager rm = ResearchManager.getInstance();
                    System.out.println("Выберите сортировку: 1. По цитированиям | 2. По дате | 3. По объёму (страницы)");
                    System.out.print("Ваш выбор: ");
                    String s = scanner.nextLine().trim();
                    Comparator<ResearchPaper> comp = switch (s) {
                        case "2" -> new university.research.comparators.PaperByDateComparator();
                        case "3" -> new university.research.comparators.PaperByLengthComparator();
                        default  -> new university.research.comparators.PaperByCitationsComparator();
                    };
                    rm.printAllPapers(comp);
                }
                case "6" -> {
                    System.out.print("Введите год для анализа (например, 2026): ");
                    try {
                        int year = Integer.parseInt(scanner.nextLine().trim());
                        ResearchManager.getInstance().printTopCitedResearcherOfYear(year);
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Ошибка: Введите корректный числовой год.");
                    }
                }
                case "7" -> handleCreateResearchProject(researcher, scanner);
                case "8" -> {
                    List<ResearchProject> projects = researcher.getProjects();
                    if (projects.isEmpty()) { System.out.println("Вы пока не участвуете в научных проектах."); return; }
                    projects.forEach(System.out::println);
                }
                case "9" -> inResearchMenu = false;
                default  -> System.out.println("Неверный выбор.");
            }
        }
    }

    /**
     * ПУБЛИЧНЫЙ МЕТОД: Создание научных проектов. Доступен для GradStudentMenuHandler.
     */
    public static void handleCreateResearchProject(Researcher researcher, Scanner scanner) {
        System.out.println("\n--- Инициализация научного проекта (Research Project) ---");
        System.out.print("Введите уникальный ID проекта: "); String id = scanner.nextLine().trim();
        System.out.print("Введите научную тему проекта (Topic): "); String topic = scanner.nextLine().trim();

        if (id.isEmpty() || topic.isEmpty()) {
            System.out.println("❌ Ошибка: ID и тема проекта не могут быть пустыми.");
            return;
        }

        ResearchProject project = new ResearchProject(id, topic);
        try {
            project.addParticipant(researcher);
        } catch (university.exceptions.NotAResearcherException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
            return;
        }

        researcher.addProject(project);
        System.out.println("✅ Научный проект успешно открыт: " + project);

        System.out.print("Хотите добавить коллегу в качестве участника проекта? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            List<Teacher> teachers = university.getUsers().stream()
                    .filter(u -> u instanceof Teacher).map(u -> (Teacher) u).toList();

            teachers.forEach(t -> System.out.printf("  login: %-15s | ФИО: %s | h-index: %d%n",
                    t.getLogin(), t.getFullName(), t.calculateHIndex()));

            System.out.print("Введите логин преподавателя: ");
            String tLogin = scanner.nextLine().trim();
            Teacher teacher = teachers.stream()
                    .filter(t -> t.getLogin().equalsIgnoreCase(tLogin)).findFirst().orElse(null);

            if (teacher == null) { System.out.println("❌ Ошибка: Преподаватель не найден."); return; }
            try {
                project.addParticipant(teacher);
                System.out.println("✅ " + teacher.getFullName() + " успешно добавлен в команду проекта.");
            } catch (university.exceptions.NotAResearcherException e) {
                System.out.println("❌ Ошибка: " + e.getMessage());
            }
        }
    }

    private static void handlePutMark(Teacher teacher, Scanner scanner) {
        System.out.println("\n--- Выставление академических баллов (Журнал оценок) ---");
        List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u).toList();
        if (students.isEmpty()) { System.out.println("В системе нет зарегистрированных студентов."); return; }

        students.forEach(s -> System.out.printf("  login: %-15s | ФИО: %s%n", s.getLogin(), s.getFullName()));

        System.out.print("Введите логин студента: ");
        String sLogin = scanner.nextLine().trim();
        Student student = students.stream()
                .filter(s -> s.getLogin().equalsIgnoreCase(sLogin)).findFirst().orElse(null);
        if (student == null) { System.out.println("❌ Ошибка: Студент не найден."); return; }

        System.out.println("\nДоступные учебные курсы:");
        university.getCourses().forEach(c -> System.out.println("  " + c));
        System.out.print("Введите код дисциплины: ");
        String code = scanner.nextLine().trim();
        Course course = university.getCourses().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
        if (course == null) { System.out.println("❌ Ошибка: Курс не найден."); return; }

        try {
            System.out.print("Первая аттестация (Attestation 1, 0-30): ");
            double att1 = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Вторая аттестация (Attestation 2, 0-30): ");
            double att2 = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Финальный экзамен (Final Exam, 0-40): ");
            double fin = Double.parseDouble(scanner.nextLine().trim());

            Mark mark = new Mark(course, student);
            mark.setAtt1(att1);
            mark.setAtt2(att2);
            mark.setFinalExam(fin);

            teacher.putMark(student, course, mark);
            System.out.printf("✅ Оценка успешно сохранена: %.1f баллов (%s)%n", mark.getTotal(), mark.getLetterGrade());
        } catch (NumberFormatException e) {
            System.out.println("❌ Ошибка ввода: Баллы должны быть числовыми.");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Критическая ошибка валидации: " + e.getMessage());
        }
    }

    private static void handleSendComplaint(Teacher teacher, Scanner scanner) {
        System.out.println("\n--- Оформление жалобы в Деканат ---");
        List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u).toList();
        if (students.isEmpty()) { System.out.println("В системе нет студентов для подачи претензий."); return; }

        students.forEach(s -> System.out.printf("  login: %-15s | ФИО: %s%n", s.getLogin(), s.getFullName()));

        System.out.print("Введите логин нарушителя: ");
        String sLogin = scanner.nextLine().trim();
        Student student = students.stream()
                .filter(s -> s.getLogin().equalsIgnoreCase(sLogin)).findFirst().orElse(null);
        if (student == null) { System.out.println("❌ Ошибка: Указанный студент не найден."); return; }

        List<Manager> managers = university.getUsers().stream()
                .filter(u -> u instanceof Manager).map(u -> (Manager) u).toList();
        if (managers.isEmpty()) { System.out.println("❌ Критическая ошибка: В системе нет менеджеров/деканов."); return; }
        Manager dean = managers.get(0);

        System.out.println("Выберите уровень важности: 1. LOW | 2. MEDIUM | 3. HIGH");
        System.out.print("Ваш выбор: ");
        String urg = scanner.nextLine().trim();
        UrgencyLevel level = switch (urg) {
            case "2" -> UrgencyLevel.MEDIUM;
            case "3" -> UrgencyLevel.HIGH;
            default  -> UrgencyLevel.LOW;
        };

        teacher.sendComplaint(student, dean, level);
        System.out.println("✅ Официальное заявление успешно направлено в обработку Деканату.");
    }
}