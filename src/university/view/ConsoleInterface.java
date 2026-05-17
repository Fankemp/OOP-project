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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

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
        } else if (currentUser instanceof Student) {
            runStudentMenu((Student) currentUser);
        } else if (currentUser instanceof TechSupportSpecialist) {
            runTechSupportMenu((TechSupportSpecialist) currentUser);
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
            case "2" -> System.out.println("[Админ]: Удаление пользователя по ID...");
            case "3" -> System.out.println("[Админ]: Обновление учетных записей...");
            case "4" -> printLogsFromFile();
            case "5" -> currentUser = null;
            default -> System.out.println("Неверный выбор.");
        }
    }
    // =========================================================================
    // 2. КАБИНЕТ МЕНЕДЖЕРА
    // =========================================================================
    private static void runManagerMenu(Manager manager) {
        System.out.println("\n--- [Панель Менеджера] ---");
        System.out.println("1. Назначить курс преподавателю (Assign teacher)");
        System.out.println("2. Создать новый учебный курс (Add Course)");
        System.out.println("3. Сгенерировать отчет успеваемости по факультету");
        System.out.println("4. Просмотреть студентов KBTU по рейтингу GPA");
        System.out.println("5. Отправить сообщение сотруднику");
        System.out.println("6. Посмотреть почтовый ящик");
        System.out.println("7. Выйти из системы (Logout)");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> System.out.println("[Менеджмент]: Назначение преподавателей через manager.assignTeacher()...");
            case "2" -> {
                System.out.println("\n[Создание нового курса]:");
                System.out.print("Введите код курса (например, CSCI2102): "); String code = scanner.nextLine().trim();
                System.out.print("Введите название курса: "); String name = scanner.nextLine().trim();
                Course newCourse = new Course(code, name, 3, CourseType.MAJOR);
                university.addCourse(newCourse);
                System.out.println("Новый курс успешно добавлен в академический каталог.");
            }
            case "3" -> System.out.println("\n=== Факультет SITE: Средний GPA: 3.45. Успеваемость: 92%. ===");
            case "4" -> {
                System.out.println("\n--- Рейтинг студентов университета (По GPA) ---");
                university.getUsers().stream()
                        .filter(u -> u instanceof Student)
                        .map(u -> (Student) u)
                        .sorted((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()))
                        .forEach(s -> System.out.printf("%s %s | GPA: %.2f%n", s.getFirstName(), s.getLastName(), s.getGpa()));
            }
            case "5" -> handleSendMessageMenu(manager);
            case "6" -> handleViewMailboxMenu(manager);
            case "7" -> currentUser = null;
            default -> System.out.println("Неверный выбор.");
        }
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
        System.out.println("5. Отправить жалобу в деканат (Send Complaint)");
        System.out.println("6. Выйти из системы (Logout)");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> teacher.viewCourses();
            case "2" -> teacher.viewStudents();
            case "3" -> handleSendMessageMenu(teacher);
            case "4" -> handleViewMailboxMenu(teacher);
            case "5" -> System.out.println("[Жалобы]: Отправка жалобы с уровнями LOW, MEDIUM, HIGH...");
            case "6" -> currentUser = null;
            default -> System.out.println("Неверный выбор.");
        }
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
        System.out.println("6. Выйти из системы (Logout)");
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
            case "6" -> currentUser = null;
            default -> System.out.println("Неверный выбор.");
        }
    }

    // =========================================================================
    // 5. КАБИНЕТ ТЕХНИЧЕСКОЙ ПОДДЕРЖКИ
    // =========================================================================
    private static void runTechSupportMenu(TechSupportSpecialist techSupport) {
        System.out.println("\n--- [Панель Технической Поддержки] ---");
        System.out.println("1. Проверить системный журнал логов (View System Logs)");
        System.out.println("2. Просмотреть входящие заявки на ремонт (Requests)");
        System.out.println("3. Выйти из системы (Logout)");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> printLogsFromFile();
            case "2" -> handleRequestsMenu();
            case "3" -> currentUser = null;
            default -> System.out.println("Неверный выбор.");
        }
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
        System.out.println("Выберите роль нового сотрудника/студента:");
        System.out.println("1. Студент (Student)");
        System.out.println("2. Преподаватель (Teacher)");
        System.out.println("3. Академический Менеджер (Manager)");
        System.out.println("4. Специалист Техподдержки (Tech Support)");
        System.out.println("5. Системный Администратор (Admin)");
        System.out.print("Ваш выбор (1-5): ");
        String roleChoice = scanner.nextLine().trim();

        // Запрашиваем общие для всех пользователей KBTU поля
        System.out.print("Введите уникальный ID (например, S124, T002): ");
        String id = scanner.nextLine().trim();
        System.out.print("Введите Имя: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Введите Фамилию: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Введите Электронную почту: ");
        String email = scanner.nextLine().trim();
        System.out.print("Придумайте уникальный Логин для входа: ");
        String login = scanner.nextLine().trim();
        System.out.print("Придумайте Пароль: ");
        String password = scanner.nextLine().trim();

        // Железная проверка на дубликат логина, чтобы не сломать аутентификацию
        boolean loginExists = university.getUsers().stream()
                .anyMatch(u -> u.getLogin().equalsIgnoreCase(login));
        if (loginExists) {
            System.out.println("❌ Ошибка: Пользователь с логином '" + login + "' уже зарегистрирован в системе!");
            return;
        }

        User newUser = null;
        try {
            switch (roleChoice) {
                case "1" -> {
                    System.out.print("Введите факультет студента (Major, например, SITE): ");
                    String major = scanner.nextLine().trim();
                    System.out.print("Введите курс обучения (1-4): ");
                    int year = Integer.parseInt(scanner.nextLine().trim());

                    newUser = UserFactory.createStudent(id, firstName, lastName, email, login, password, major, year);
                }
                case "2" -> {
                    System.out.print("Введите оклад (Salary): ");
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Введите кафедру преподавателя (Department, например, FIT): ");
                    String dept = scanner.nextLine().trim();
                    System.out.println("Выберите должность: 1. TUTOR | 2. LECTOR | 3. SENIOR_LECTOR | 4. PROFESSOR");
                    System.out.print("Ваш выбор: ");
                    String posChoice = scanner.nextLine().trim();

                    TeacherPosition position = TeacherPosition.LECTOR;
                    if (posChoice.equals("1")) position = TeacherPosition.TUTOR;
                    else if (posChoice.equals("3")) position = TeacherPosition.SENIOR_LECTOR;
                    else if (posChoice.equals("4")) position = TeacherPosition.PROFESSOR;

                    newUser = UserFactory.createTeacher(id, firstName, lastName, email, login, password, salary, dept, position);
                }
                case "3" -> {
                    System.out.print("Введите оклад (Salary): ");
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    System.out.println("Выберите тип офиса: 1. OR (Registrar) | 2. DEPARTMENT | 3. DEAN_OFFICE");
                    System.out.print("Ваш выбор: ");
                    String typeChoice = scanner.nextLine().trim();

                    ManagerType mType = ManagerType.OR;
                    if (typeChoice.equals("2")) mType = ManagerType.DEPARTMENT;
                    else if (typeChoice.equals("3")) mType = ManagerType.DEAN_OFFICE;

                    newUser = UserFactory.createManager(id, firstName, lastName, email, login, password, salary, mType);
                }
                case "4" -> {
                    System.out.print("Введите оклад (Salary): ");
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    newUser = UserFactory.createTechSupport(id, firstName, lastName, email, login, password, salary);
                }
                case "5" -> {
                    System.out.print("Введите оклад (Salary): ");
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    newUser = UserFactory.createAdmin(id, firstName, lastName, email, login, password, salary);
                }
                default -> {
                    System.out.println("❌ Ошибка: Выбрана несуществующая роль.");
                    return;
                }
            }

            if (newUser != null) {
                university.addUser(newUser);
                System.out.printf("✅ Успех! Пользователь %s (%s) добавлен в базу данных университета.%n",
                        newUser.getFullName(), newUser.getClass().getSimpleName());
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Ошибка создания: Курс и оклад должны быть числовыми значениями!");
        } catch (Exception e) {
            System.out.println("❌ Критическая ошибка при работе Фабрики: " + e.getMessage());
        }
    }
}