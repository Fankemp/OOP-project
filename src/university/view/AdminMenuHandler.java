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
        System.out.println("   " + Lang.t("Панель Администратора KBTU", "Admin Panel KBTU", "KBTU Әкімші панелі"));
        System.out.println("========================================");
        System.out.println("1. " + Lang.t("Добавить пользователя", "Add User", "Пайдаланушы қосу"));
        System.out.println("2. " + Lang.t("Удалить пользователя", "Remove User", "Пайдаланушыны жою"));
        System.out.println("3. " + Lang.t("Изменить данные", "Update User", "Деректерді өзгерту"));
        System.out.println("4. " + Lang.t("Просмотреть логи", "See Logs", "Журналды көру"));
        System.out.println("5. " + Lang.t("Выйти", "Logout", "Шығу"));
        System.out.println("6. " + Lang.t("Заявка в техподдержку", "Send Tech Request", "Техқолдауға өтініш"));
        System.out.println("7. " + Lang.t("Сменить язык", "Switch Language", "Тілді ауыстыру"));
        System.out.print(Lang.t("Выберите действие: ", "Choose action: ", "Әрекетті таңдаңыз: "));

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleCreateUserMenu(scanner);
            case "2" -> handleRemoveUser(scanner);
            case "3" -> handleUpdateUser(scanner);
            case "4" -> printLogsFromFile();
            case "5" -> {
                System.out.println(Lang.t("Сессия завершена.", "Session ended.", "Сессия аяқталды."));
                return true;
            }
            case "6" -> TechSupportMenuHandler.handleCreateRequest(admin, scanner);
            case "7" -> SharedMenuComponents.handleSwitchLanguage(scanner);
            default -> System.out.println(Lang.t("Ошибка: Неверный пункт меню.", "Error: Invalid choice.", "Қате: Жарамсыз таңдау."));
        }
        return false;
    }

    private static void handleCreateUserMenu(Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Создание пользователя", "Create User", "Пайдаланушы жасау") + " ---");
        System.out.println("1. " + Lang.t("Студент", "Student", "Студент"));
        System.out.println("2. " + Lang.t("Магистрант/PhD", "Graduate Student", "Магистрант/PhD"));
        System.out.println("3. " + Lang.t("Преподаватель", "Teacher", "Оқытушы"));
        System.out.println("4. " + Lang.t("Менеджер", "Manager", "Менеджер"));
        System.out.println("5. " + Lang.t("Техподдержка", "Tech Support", "Техқолдау"));
        System.out.println("6. " + Lang.t("Администратор", "Admin", "Әкімші"));
        System.out.print(Lang.t("Ваш выбор (1-6): ", "Your choice (1-6): ", "Таңдауыңыз (1-6): "));
        String roleChoice = scanner.nextLine().trim();

        System.out.print(Lang.t("ID: ", "ID: ", "ID: "));
        String id = scanner.nextLine().trim();
        System.out.print(Lang.t("Имя: ", "First name: ", "Аты: "));
        String firstName = scanner.nextLine().trim();
        System.out.print(Lang.t("Фамилия: ", "Last name: ", "Тегі: "));
        String lastName = scanner.nextLine().trim();
        System.out.print(Lang.t("Email: ", "Email: ", "Email: "));
        String email = scanner.nextLine().trim();
        System.out.print(Lang.t("Логин: ", "Login: ", "Логин: "));
        String login = scanner.nextLine().trim();
        System.out.print(Lang.t("Пароль: ", "Password: ", "Құпия сөз: "));
        String password = scanner.nextLine().trim();

        boolean loginExists = university.getUsers().stream()
                .anyMatch(u -> u.getLogin().equalsIgnoreCase(login));
        if (loginExists) {
            System.out.println(Lang.t("Ошибка: логин уже занят!", "Error: login already taken!", "Қате: логин бос емес!"));
            return;
        }

        User newUser = null;
        try {
            switch (roleChoice) {
                case "1" -> {
                    System.out.print(Lang.t("Специальность: ", "Major: ", "Мамандық: "));
                    String major = scanner.nextLine().trim();
                    System.out.print(Lang.t("Курс (1-4): ", "Year (1-4): ", "Курс (1-4): "));
                    int year = Integer.parseInt(scanner.nextLine().trim());
                    newUser = UserFactory.createStudent(id, firstName, lastName, email, login, password, major, year);
                }
                case "2" -> {
                    System.out.print(Lang.t("Специальность: ", "Major: ", "Мамандық: "));
                    String major = scanner.nextLine().trim();
                    System.out.print(Lang.t("Курс (1-2): ", "Year (1-2): ", "Курс (1-2): "));
                    int year = Integer.parseInt(scanner.nextLine().trim());
                    System.out.println(Lang.t("Степень: 1. MASTER | 2. PHD", "Degree: 1. MASTER | 2. PHD", "Дәреже: 1. MASTER | 2. PHD"));
                    System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));
                    DegreeType degree = scanner.nextLine().trim().equals("2") ? DegreeType.PHD : DegreeType.MASTER;
                    newUser = new GraduateStudent(id, firstName, lastName, email, login, password, major, year, degree);
                }
                case "3" -> {
                    System.out.print(Lang.t("Оклад: ", "Salary: ", "Жалақы: "));
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print(Lang.t("Кафедра: ", "Department: ", "Кафедра: "));
                    String dept = scanner.nextLine().trim();
                    System.out.println(Lang.t("Должность: 1. TUTOR | 2. LECTOR | 3. SENIOR_LECTOR | 4. PROFESSOR",
                            "Position: 1. TUTOR | 2. LECTOR | 3. SENIOR_LECTOR | 4. PROFESSOR",
                            "Лауазым: 1. TUTOR | 2. LECTOR | 3. SENIOR_LECTOR | 4. PROFESSOR"));
                    System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));
                    TeacherPosition position = switch (scanner.nextLine().trim()) {
                        case "1" -> TeacherPosition.TUTOR;
                        case "3" -> TeacherPosition.SENIOR_LECTOR;
                        case "4" -> TeacherPosition.PROFESSOR;
                        default  -> TeacherPosition.LECTOR;
                    };
                    newUser = UserFactory.createTeacher(id, firstName, lastName, email, login, password, salary, dept, position);
                }
                case "4" -> {
                    System.out.print(Lang.t("Оклад: ", "Salary: ", "Жалақы: "));
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    System.out.println(Lang.t("Тип: 1. OR | 2. DEPARTMENT | 3. DEAN_OFFICE",
                            "Type: 1. OR | 2. DEPARTMENT | 3. DEAN_OFFICE",
                            "Түрі: 1. OR | 2. DEPARTMENT | 3. DEAN_OFFICE"));
                    System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));
                    ManagerType mType = switch (scanner.nextLine().trim()) {
                        case "2" -> ManagerType.DEPARTMENT;
                        case "3" -> ManagerType.DEAN_OFFICE;
                        default  -> ManagerType.OR;
                    };
                    newUser = UserFactory.createManager(id, firstName, lastName, email, login, password, salary, mType);
                }
                case "5" -> {
                    System.out.print(Lang.t("Оклад: ", "Salary: ", "Жалақы: "));
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    newUser = UserFactory.createTechSupport(id, firstName, lastName, email, login, password, salary);
                }
                case "6" -> {
                    System.out.print(Lang.t("Оклад: ", "Salary: ", "Жалақы: "));
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    newUser = UserFactory.createAdmin(id, firstName, lastName, email, login, password, salary);
                }
                default -> {
                    System.out.println(Lang.t("Неверный выбор.", "Invalid choice.", "Жарамсыз таңдау."));
                    return;
                }
            }

            if (newUser != null) {
                university.addUser(newUser);
                System.out.printf(Lang.t("✅ Пользователь %s (%s) добавлен.%n",
                        "✅ User %s (%s) added.%n",
                        "✅ Пайдаланушы %s (%s) қосылды.%n"),
                        newUser.getFullName(), newUser.getClass().getSimpleName());
            }
        } catch (NumberFormatException e) {
            System.out.println(Lang.t("Ошибка: введите числовое значение.", "Error: enter a number.", "Қате: сан енгізіңіз."));
        } catch (Exception e) {
            System.out.println(Lang.t("Ошибка: ", "Error: ", "Қате: ") + e.getMessage());
        }
    }

    private static void handleRemoveUser(Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Удаление пользователя", "Remove User", "Пайдаланушыны жою") + " ---");
        if (university.getUsers().isEmpty()) {
            System.out.println(Lang.t("База пуста.", "No users found.", "Дерекқор бос."));
            return;
        }
        university.getUsers().forEach(u ->
                System.out.printf("  ID: %-8s | %-25s | %s%n",
                        u.getId(), u.getFullName(), u.getClass().getSimpleName()));
        System.out.print(Lang.t("Введите ID для удаления: ", "Enter ID to remove: ", "Жою үшін ID енгізіңіз: "));
        String id = scanner.nextLine().trim();
        boolean removed = university.removeUser(id);
        System.out.println(removed
                ? Lang.t("✅ Пользователь удалён.", "✅ User removed.", "✅ Пайдаланушы жойылды.")
                : Lang.t("❌ Пользователь не найден.", "❌ User not found.", "❌ Пайдаланушы табылмады."));
    }

    private static void handleUpdateUser(Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Изменение данных", "Update User", "Деректерді өзгерту") + " ---");
        if (university.getUsers().isEmpty()) {
            System.out.println(Lang.t("Нет пользователей.", "No users.", "Пайдаланушылар жоқ."));
            return;
        }
        university.getUsers().forEach(u ->
                System.out.printf("  ID: %-8s | %-25s | %s%n",
                        u.getId(), u.getFullName(), u.getLogin()));
        System.out.print(Lang.t("Введите ID: ", "Enter ID: ", "ID енгізіңіз: "));
        String id = scanner.nextLine().trim();
        User target = university.findUserById(id);
        if (target == null) {
            System.out.println(Lang.t("❌ Не найден.", "❌ Not found.", "❌ Табылмады."));
            return;
        }
        System.out.println("1. " + Lang.t("Имя", "First Name", "Аты"));
        System.out.println("2. " + Lang.t("Фамилия", "Last Name", "Тегі"));
        System.out.println("3. " + Lang.t("Email", "Email", "Email"));
        System.out.println("4. " + Lang.t("Пароль", "Password", "Құпия сөз"));
        System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));
        String choice = scanner.nextLine().trim();
        System.out.print(Lang.t("Новое значение: ", "New value: ", "Жаңа мән: "));
        String val = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> target.setFirstName(val);
            case "2" -> target.setLastName(val);
            case "3" -> target.setEmail(val);
            case "4" -> target.setPassword(val);
            default  -> { System.out.println(Lang.t("Отменено.", "Cancelled.", "Бас тартылды.")); return; }
        }
        System.out.println(Lang.t("✅ Данные обновлены.", "✅ Updated.", "✅ Жаңартылды."));
    }

    private static void printLogsFromFile() {
        File logFile = new File("university.log");
        if (!logFile.exists() || logFile.length() == 0) {
            System.out.println(Lang.t("Журнал пуст.", "Log is empty.", "Журнал бос."));
            return;
        }
        System.out.println("\n=== " + Lang.t("СИСТЕМНЫЙ ЖУРНАЛ", "SYSTEM LOG", "ЖҮЙЕ ЖУРНАЛЫ") + " ===");
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) System.out.println(line);
        } catch (IOException e) {
            System.out.println(Lang.t("Ошибка чтения логов: ", "Log read error: ", "Журнал оқу қатесі: ") + e.getMessage());
        }
        System.out.println("==========================================");
    }
}