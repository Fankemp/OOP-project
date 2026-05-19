package university.view;

import university.storage.University;
import university.storage.UserFactory;
import university.users.Admin;
import university.users.User;
import university.users.GraduateStudent;
import university.enums.DegreeType;
import university.enums.TeacherPosition;
import university.enums.ManagerType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class AdminMenuHandler {
    private static final University university = University.getInstance();

    public static boolean show(Admin admin, Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("       [Панель Администратора KBTU]     ");
        System.out.println("========================================");
        System.out.println("1. Добавить нового пользователя (Add User)");
        System.out.println("2. Удалить пользователя из системы (Remove User)");
        System.out.println("3. Изменить данные учетной записи (Update User)");
        System.out.println("4. Просмотреть системный журнал действий (See Logs)");
        System.out.println("5. Выйти из учетной записи (Logout)");
        System.out.println("6. Отправить заявку в техподдержку (Send Tech Request)");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleCreateUserMenu(scanner);
            case "2" -> handleRemoveUser(scanner);
            case "3" -> handleUpdateUser(scanner);
            case "4" -> printLogsFromFile();
            case "5" -> {
                System.out.println("Сессия администратора завершена.");
                return true;
            }
            case "6" -> TechSupportMenuHandler.handleCreateRequest(admin, scanner);
            default -> System.out.println("Ошибка: Неверный пункт меню. Попробуйте еще раз.");
        }
        return false;
    }

    /**
     * Интерактивное создание учетной записи пользователя через Фабрику
     */
    private static void handleCreateUserMenu(Scanner scanner) {
        System.out.println("\n--- Создание нового пользователя ---");
        System.out.println("1. Студент (Student)");
        System.out.println("2. Магистрант / PhD Докторант (Graduate Student)");
        System.out.println("3. Преподаватель (Teacher)");
        System.out.println("4. Академический Менеджер (Manager)");
        System.out.println("5. Специалист техподдержки (Tech Support)");
        System.out.println("6. Системный Администратор (Admin)");
        System.out.print("Ваш выбор (1-6): ");
        String roleChoice = scanner.nextLine().trim();

        System.out.print("Введите уникальный ID (например, S124, T002): ");
        String id = scanner.nextLine().trim();
        System.out.print("Введите Имя: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Введите Фамилию: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Введите электронную почту: ");
        String email = scanner.nextLine().trim();
        System.out.print("Придумайте уникальный Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Придумайте Пароль: ");
        String password = scanner.nextLine().trim();

        // Проверка логина на дубликаты
        boolean loginExists = university.getUsers().stream()
                .anyMatch(u -> u.getLogin().equalsIgnoreCase(login));
        if (loginExists) {
            System.out.println("❌ Ошибка: логин '" + login + "' уже используется в системе!");
            return;
        }

        User newUser = null;
        try {
            switch (roleChoice) {
                case "1" -> {
                    System.out.print("Специальность (Major, например, SITE): ");
                    String major = scanner.nextLine().trim();
                    System.out.print("Курс обучения (1-4): ");
                    int year = Integer.parseInt(scanner.nextLine().trim());
                    newUser = UserFactory.createStudent(id, firstName, lastName, email, login, password, major, year);
                }
                case "2" -> {
                    System.out.print("Специальность (Major): ");
                    String major = scanner.nextLine().trim();
                    System.out.print("Курс (1-2): ");
                    int year = Integer.parseInt(scanner.nextLine().trim());
                    System.out.println("Ученая степень: 1. MASTER | 2. PHD");
                    System.out.print("Ваш выбор: ");
                    DegreeType degree = scanner.nextLine().trim().equals("2") ? DegreeType.PHD : DegreeType.MASTER;
                    newUser = new GraduateStudent(id, firstName, lastName, email, login, password, major, year, degree);
                }
                case "3" -> {
                    System.out.print("Оклад (Salary): ");
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Кафедра (Department): ");
                    String dept = scanner.nextLine().trim();
                    System.out.println("Должность: 1. TUTOR | 2. LECTOR | 3. SENIOR_LECTOR | 4. PROFESSOR");
                    System.out.print("Ваш выбор: ");
                    TeacherPosition position = switch (scanner.nextLine().trim()) {
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
                    System.out.println("Тип офиса менеджера: 1. OR | 2. DEPARTMENT | 3. DEAN_OFFICE");
                    System.out.print("Ваш выбор: ");
                    ManagerType mType = switch (scanner.nextLine().trim()) {
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
                    System.out.println("❌ Ошибка: Неверный выбор роли.");
                    return;
                }
            }

            if (newUser != null) {
                university.addUser(newUser);
                System.out.printf("✅ Успех! Пользователь %s (%s) успешно сохранен в базу данных KBTU.%n",
                        newUser.getFullName(), newUser.getClass().getSimpleName());
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Ошибка ввода: Числовые параметры (курс, оклад) заполнены неверно.");
        } catch (Exception e) {
            System.out.println("❌ Ошибка при генерации сущности Фабрикой: " + e.getMessage());
        }
    }

    private static void handleRemoveUser(Scanner scanner) {
        System.out.println("\n--- Удаление пользователя из базы данных ---");
        if (university.getUsers().isEmpty()) {
            System.out.println("База данных пользователей пуста.");
            return;
        }

        university.getUsers().forEach(u ->
                System.out.printf("  ID: %-8s | ФИО: %-25s | Роль: %s%n",
                        u.getId(), u.getFullName(), u.getClass().getSimpleName()));

        System.out.print("\nВведите уникальный ID пользователя для удаления: ");
        String id = scanner.nextLine().trim();

        boolean removed = university.removeUser(id);
        if (removed) {
            System.out.println("✅ Пользователь успешно удален из репозитория университета.");
        } else {
            System.out.println("❌ Ошибка: Пользователь с ID '" + id + "' не найден.");
        }
    }

    private static void handleUpdateUser(Scanner scanner) {
        System.out.println("\n--- Модификация профиля пользователя ---");
        if (university.getUsers().isEmpty()) {
            System.out.println("В системе нет зарегистрированных пользователей.");
            return;
        }

        university.getUsers().forEach(u ->
                System.out.printf("  ID: %-8s | ФИО: %-25s | Логин: %s%n",
                        u.getId(), u.getFullName(), u.getLogin()));

        System.out.print("\nВведите ID пользователя для редактирования: ");
        String id = scanner.nextLine().trim();
        User target = university.findUserById(id);

        if (target == null) {
            System.out.println("❌ Ошибка: Пользователь с таким идентификатором не найден.");
            return;
        }

        System.out.println("\nКакое поле вы хотите обновить?");
        System.out.println("1. Имя (First Name)");
        System.out.println("2. Фамилия (Last Name)");
        System.out.println("3. Электронная почта (Email)");
        System.out.println("4. Пароль доступа (Password)");
        System.out.print("Ваш выбор: ");
        String choice = scanner.nextLine().trim();

        System.out.print("Введите новое значение: ");
        String val = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> target.setFirstName(val);
            case "2" -> target.setLastName(val);
            case "3" -> target.setEmail(val);
            case "4" -> target.setPassword(val);
            default  -> {
                System.out.println("❌ Операция отменена: выбран некорректный параметр.");
                return;
            }
        }
        System.out.println("✅ Данные успешно изменены для аккаунта: " + target.getFullName());
    }

    private static void printLogsFromFile() {
        File logFile = new File("university.log");
        if (!logFile.exists() || logFile.length() == 0) {
            System.out.println("Журнал системного аудита пуст или еще не сгенерирован.");
            return;
        }

        System.out.println("\n=== ВЫЧИТКА СИСТЕМНОГО ЖУРНАЛА (university.log) ===");
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("❌ Критическая ошибка при чтении файла логов: " + e.getMessage());
        }
        System.out.println("====================================================");
    }
}