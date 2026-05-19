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

public class TeacherMenuHandler {
    private static final University university = University.getInstance();

    public static boolean show(Teacher teacher, Scanner scanner) {
        System.out.println("\n--- [" + Lang.t("Панель Преподавателя KBTU", "Teacher Panel KBTU", "KBTU Оқытушы панелі") + "] ---");
        System.out.println("1. " + Lang.t("Просмотреть учебные дисциплины", "View Courses", "Пәндерді көру"));
        System.out.println("2. " + Lang.t("Список студентов на моих курсах", "View Students", "Студенттер тізімі"));
        System.out.println("3. " + Lang.t("Отправить сообщение", "Send Message", "Хабар жіберу"));
        System.out.println("4. " + Lang.t("Почтовый ящик", "Mailbox", "Пошта жәшігі"));
        System.out.println("5. " + Lang.t("Выставить оценки", "Put Mark", "Баға қою"));
        System.out.println("6. " + Lang.t("Жалоба в деканат", "Send Complaint", "Деканатқа шағым"));
        System.out.println("7. " + Lang.t("Меню исследователя", "Research Panel", "Зерттеуші мәзірі"));
        System.out.println("8. " + Lang.t("Выйти", "Logout", "Шығу"));
        System.out.println("9. " + Lang.t("Сменить язык", "Switch Language", "Тілді ауыстыру"));
        System.out.print(Lang.t("Выберите действие: ", "Choose action: ", "Әрекетті таңдаңыз: "));

        switch (scanner.nextLine().trim()) {
            case "1" -> teacher.viewCourses();
            case "2" -> teacher.viewStudents();
            case "3" -> CommunicationHandler.handleSendMessageMenu(teacher, scanner);
            case "4" -> CommunicationHandler.handleViewMailboxMenu(teacher, scanner);
            case "5" -> handlePutMark(teacher, scanner);
            case "6" -> handleSendComplaint(teacher, scanner);
            case "7" -> handleResearcherMenu(teacher, scanner);
            case "8" -> {
                System.out.println(Lang.t("Сессия завершена.", "Session ended.", "Сессия аяқталды."));
                return true;
            }
            case "9" -> SharedMenuComponents.handleSwitchLanguage(scanner);
            default -> System.out.println(Lang.t("Неверный выбор.", "Invalid choice.", "Жарамсыз таңдау."));
        }
        return false;
    }

    public static void handleResearcherMenu(Researcher researcher, Scanner scanner) {
        boolean inResearchMenu = true;
        while (inResearchMenu) {
            System.out.println("\n--- [" + Lang.t("Меню Исследователя", "Research Panel", "Зерттеуші мәзірі") + "] ---");
            System.out.println("1. " + Lang.t("Мои научные статьи", "My Papers", "Менің мақалаларым"));
            System.out.println("2. " + Lang.t("Опубликовать статью", "Add Paper", "Мақала жариялау"));
            System.out.println("3. " + Lang.t("Рассчитать h-index", "Calculate h-index", "h-индексті есептеу"));
            System.out.println("4. " + Lang.t("Статьи по цитированиям", "Papers by Citations", "Цитата бойынша мақалалар"));
            System.out.println("5. " + Lang.t("Все статьи университета", "All University Papers", "Университет мақалалары"));
            System.out.println("6. " + Lang.t("Топ ученый года", "Top Researcher of Year", "Жылдың үздік ғалымы"));
            System.out.println("7. " + Lang.t("Создать научный проект", "Create Research Project", "Жоба жасау"));
            System.out.println("8. " + Lang.t("Мои проекты", "My Projects", "Менің жобаларым"));
            System.out.println("9. " + Lang.t("Назад", "Back", "Артқа"));
            System.out.print(Lang.t("Выберите действие: ", "Choose action: ", "Әрекетті таңдаңыз: "));

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    List<ResearchPaper> papers = researcher.getPapers();
                    if (papers.isEmpty()) {
                        System.out.println(Lang.t("Нет опубликованных работ.", "No papers yet.", "Мақалалар жоқ."));
                        return;
                    }
                    papers.forEach(System.out::println);
                }
                case "2" -> {
                    try {
                        System.out.print(Lang.t("Название: ", "Title: ", "Атауы: "));
                        String title = scanner.nextLine().trim();
                        System.out.print(Lang.t("Авторы (через запятую): ", "Authors (comma separated): ", "Авторлар (үтірмен): "));
                        List<String> authors = Arrays.asList(scanner.nextLine().split(","));
                        System.out.print(Lang.t("Журнал: ", "Journal: ", "Журнал: "));
                        String journal = scanner.nextLine().trim();
                        System.out.print(Lang.t("Начальная страница: ", "Start page: ", "Бастапқы бет: "));
                        int start = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print(Lang.t("Конечная страница: ", "End page: ", "Соңғы бет: "));
                        int end = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print(Lang.t("Год: ", "Year: ", "Жыл: "));
                        int year = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("DOI: ");
                        String doi = scanner.nextLine().trim();
                        System.out.print(Lang.t("Цитирования: ", "Citations: ", "Цитаталар: "));
                        int citations = Integer.parseInt(scanner.nextLine().trim());

                        ResearchPaper paper = new ResearchPaper(title, authors, journal, start, end,
                                LocalDate.of(year, 1, 1), doi, citations);
                        ResearchManager.getInstance().registerResearcher(researcher);
                        researcher.addPaper(paper);
                        System.out.println(Lang.t("✅ Статья зарегистрирована.", "✅ Paper added.", "✅ Мақала тіркелді."));

                        String authorName = (researcher instanceof User) ? ((User) researcher).getFullName() : "Researcher";
                        university.addNews(new News("New Research Paper Published",
                                authorName + " published: \"" + title + "\" in " + journal, "RESEARCH"));

                        Researcher top = ResearchManager.getInstance().getTopCitedResearcher();
                        if (top != null) {
                            String topName = (top instanceof User) ? ((User) top).getFullName() : "Unknown";
                            university.addNews(new News("Top Cited Researcher",
                                    topName + " is the most cited researcher at KBTU with h-index: " + top.calculateHIndex(),
                                    "RESEARCH"));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(Lang.t("❌ Ошибка: введите числа.", "❌ Error: enter numbers.", "❌ Қате: сандарды енгізіңіз."));
                    }
                }
                case "3" -> System.out.println(Lang.t("Ваш h-index: ", "Your h-index: ", "Сіздің h-индексіңіз: ") + researcher.calculateHIndex());
                case "4" -> {
                    Comparator<ResearchPaper> byCitations = (p1, p2) -> Integer.compare(p2.getCitations(), p1.getCitations());
                    researcher.printPapers(byCitations);
                }
                case "5" -> {
                    ResearchManager rm = ResearchManager.getInstance();
                    System.out.println(Lang.t("Сортировка: 1. По цитированиям | 2. По дате | 3. По объёму",
                            "Sort: 1. By citations | 2. By date | 3. By length",
                            "Сұрыптау: 1. Цитата | 2. Күні | 3. Көлемі"));
                    System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));
                    String s = scanner.nextLine().trim();
                    Comparator<ResearchPaper> comp = switch (s) {
                        case "2" -> new university.research.comparators.PaperByDateComparator();
                        case "3" -> new university.research.comparators.PaperByLengthComparator();
                        default  -> new university.research.comparators.PaperByCitationsComparator();
                    };
                    rm.printAllPapers(comp);
                }
                case "6" -> {
                    System.out.print(Lang.t("Введите год: ", "Enter year: ", "Жылды енгізіңіз: "));
                    try {
                        int year = Integer.parseInt(scanner.nextLine().trim());
                        ResearchManager.getInstance().printTopCitedResearcherOfYear(year);
                    } catch (NumberFormatException e) {
                        System.out.println(Lang.t("❌ Введите число.", "❌ Enter a number.", "❌ Сан енгізіңіз."));
                    }
                }
                case "7" -> handleCreateResearchProject(researcher, scanner);
                case "8" -> {
                    List<ResearchProject> projects = researcher.getProjects();
                    if (projects.isEmpty()) {
                        System.out.println(Lang.t("Нет проектов.", "No projects.", "Жобалар жоқ."));
                        return;
                    }
                    projects.forEach(System.out::println);
                }
                case "9" -> inResearchMenu = false;
                default -> System.out.println(Lang.t("Неверный выбор.", "Invalid choice.", "Жарамсыз таңдау."));
            }
        }
    }

    public static void handleCreateResearchProject(Researcher researcher, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Создание научного проекта", "Create Research Project", "Ғылыми жоба жасау") + " ---");
        System.out.print(Lang.t("ID проекта: ", "Project ID: ", "Жоба ID: "));
        String id = scanner.nextLine().trim();
        System.out.print(Lang.t("Тема проекта: ", "Project Topic: ", "Жоба тақырыбы: "));
        String topic = scanner.nextLine().trim();

        if (id.isEmpty() || topic.isEmpty()) {
            System.out.println(Lang.t("❌ ID и тема не могут быть пустыми.", "❌ ID and topic required.", "❌ ID және тақырып міндетті."));
            return;
        }

        ResearchProject project = new ResearchProject(id, topic);
        try {
            project.addParticipant(researcher);
        } catch (university.exceptions.NotAResearcherException e) {
            System.out.println(Lang.t("❌ Ошибка: ", "❌ Error: ", "❌ Қате: ") + e.getMessage());
            return;
        }

        researcher.addProject(project);
        System.out.println(Lang.t("✅ Проект создан: ", "✅ Project created: ", "✅ Жоба жасалды: ") + project);

        System.out.print(Lang.t("Добавить участника? (y/n): ", "Add participant? (y/n): ", "Қатысушы қосу? (y/n): "));
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            List<Teacher> teachers = university.getUsers().stream()
                    .filter(u -> u instanceof Teacher).map(u -> (Teacher) u).toList();
            teachers.forEach(t -> System.out.printf("  login: %-15s | %s | h-index: %d%n",
                    t.getLogin(), t.getFullName(), t.calculateHIndex()));
            System.out.print(Lang.t("Логин преподавателя: ", "Teacher login: ", "Оқытушы логині: "));
            String tLogin = scanner.nextLine().trim();
            Teacher teacher = teachers.stream()
                    .filter(t -> t.getLogin().equalsIgnoreCase(tLogin)).findFirst().orElse(null);
            if (teacher == null) {
                System.out.println(Lang.t("❌ Не найден.", "❌ Not found.", "❌ Табылмады."));
                return;
            }
            try {
                project.addParticipant(teacher);
                System.out.println(Lang.t("✅ Добавлен: ", "✅ Added: ", "✅ Қосылды: ") + teacher.getFullName());
            } catch (university.exceptions.NotAResearcherException e) {
                System.out.println(Lang.t("❌ Ошибка: ", "❌ Error: ", "❌ Қате: ") + e.getMessage());
            }
        }
    }

    private static void handlePutMark(Teacher teacher, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Выставление оценок", "Put Mark", "Баға қою") + " ---");
        List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u).toList();
        if (students.isEmpty()) {
            System.out.println(Lang.t("Нет студентов.", "No students.", "Студенттер жоқ."));
            return;
        }
        students.forEach(s -> System.out.printf("  login: %-15s | %s%n", s.getLogin(), s.getFullName()));
        System.out.print(Lang.t("Логин студента: ", "Student login: ", "Студент логині: "));
        String sLogin = scanner.nextLine().trim();
        Student student = students.stream()
                .filter(s -> s.getLogin().equalsIgnoreCase(sLogin)).findFirst().orElse(null);
        if (student == null) {
            System.out.println(Lang.t("❌ Студент не найден.", "❌ Student not found.", "❌ Студент табылмады."));
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
        try {
            System.out.print(Lang.t("Аттестация 1 (0-30): ", "Attestation 1 (0-30): ", "1-аттестация (0-30): "));
            double att1 = Double.parseDouble(scanner.nextLine().trim());
            System.out.print(Lang.t("Аттестация 2 (0-30): ", "Attestation 2 (0-30): ", "2-аттестация (0-30): "));
            double att2 = Double.parseDouble(scanner.nextLine().trim());
            System.out.print(Lang.t("Финальный экзамен (0-40): ", "Final Exam (0-40): ", "Финалдық емтихан (0-40): "));
            double fin = Double.parseDouble(scanner.nextLine().trim());
            Mark mark = new Mark(course, student);
            mark.setAtt1(att1);
            mark.setAtt2(att2);
            mark.setFinalExam(fin);
            teacher.putMark(student, course, mark);
            System.out.printf(Lang.t("✅ Оценка сохранена: %.1f (%s)%n", "✅ Mark saved: %.1f (%s)%n", "✅ Баға сақталды: %.1f (%s)%n"),
                    mark.getTotal(), mark.getLetterGrade());
        } catch (NumberFormatException e) {
            System.out.println(Lang.t("❌ Введите числа.", "❌ Enter numbers.", "❌ Сандарды енгізіңіз."));
        }
    }

    private static void handleSendComplaint(Teacher teacher, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Жалоба в Деканат", "Complaint to Dean", "Деканатқа шағым") + " ---");
        List<Student> students = university.getUsers().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u).toList();
        if (students.isEmpty()) {
            System.out.println(Lang.t("Нет студентов.", "No students.", "Студенттер жоқ."));
            return;
        }
        students.forEach(s -> System.out.printf("  login: %-15s | %s%n", s.getLogin(), s.getFullName()));
        System.out.print(Lang.t("Логин студента: ", "Student login: ", "Студент логині: "));
        String sLogin = scanner.nextLine().trim();
        Student student = students.stream()
                .filter(s -> s.getLogin().equalsIgnoreCase(sLogin)).findFirst().orElse(null);
        if (student == null) {
            System.out.println(Lang.t("❌ Не найден.", "❌ Not found.", "❌ Табылмады."));
            return;
        }
        List<Manager> managers = university.getUsers().stream()
                .filter(u -> u instanceof Manager).map(u -> (Manager) u).toList();
        if (managers.isEmpty()) {
            System.out.println(Lang.t("❌ Нет менеджеров.", "❌ No managers.", "❌ Менеджерлер жоқ."));
            return;
        }
        System.out.println(Lang.t("Уровень: 1. LOW | 2. MEDIUM | 3. HIGH",
                "Level: 1. LOW | 2. MEDIUM | 3. HIGH",
                "Деңгей: 1. LOW | 2. MEDIUM | 3. HIGH"));
        System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));
        UrgencyLevel level = switch (scanner.nextLine().trim()) {
            case "2" -> UrgencyLevel.MEDIUM;
            case "3" -> UrgencyLevel.HIGH;
            default  -> UrgencyLevel.LOW;
        };
        teacher.sendComplaint(student, managers.get(0), level);
        System.out.println(Lang.t("✅ Жалоба отправлена.", "✅ Complaint sent.", "✅ Шағым жіберілді."));
    }
}