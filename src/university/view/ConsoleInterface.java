package university.view;

import university.storage.University;
import university.users.*;
import java.util.Optional;
import java.util.Scanner;
import university.enums.Language;

public class ConsoleInterface {
    private static final University university = University.getInstance();
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void start() {
        while (true) {
            if (currentUser != null) {
                routeUserToMenu();
                continue;
            }

            System.out.println("\n========================================");
            System.out.println(Lang.t("   Добро пожаловать в систему KBTU", "   Welcome to KBTU System", "   KBTU жүйесіне қош келдіңіз"));
            System.out.println("========================================");
            System.out.println("1. " + Lang.t("Войти в систему", "Login", "Жүйеге кіру"));
            System.out.println("2. " + Lang.t("Сменить язык", "Switch Language", "Тілді ауыстыру"));
            System.out.println("3. " + Lang.t("Выйти из программы", "Exit", "Бағдарламадан шығу"));
            System.out.print(Lang.t("Выберите действие: ", "Choose action: ", "Әрекетті таңдаңыз: "));

            switch (scanner.nextLine().trim()) {
                case "1" -> showLoginMenu();
                case "2" -> {
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
                    System.out.println(Lang.t("✅ Язык изменён.", "✅ Language changed.", "✅ Тіл өзгертілді."));
                }
                case "3" -> {
                    System.out.println(Lang.t("\n[Система]: Сохранение данных...",
                            "\n[System]: Saving data...",
                            "\n[Жүйе]: Деректер сақталуда..."));
                    university.save();
                    System.out.println(Lang.t("Данные сохранены. До встречи!",
                            "Data saved. Goodbye!",
                            "Деректер сақталды. Сау болыңыз!"));
                    return;
                }
                default -> System.out.println(Lang.t("❌ Неверный выбор.", "❌ Invalid choice.", "❌ Жарамсыз таңдау."));
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n--- " + Lang.t("Форма авторизации", "Login Form", "Авторизация нысаны") + " ---");
        System.out.print(Lang.t("Логин: ", "Login: ", "Логин: "));
        String login = scanner.nextLine().trim();
        System.out.print(Lang.t("Пароль: ", "Password: ", "Құпия сөз: "));
        String password = scanner.nextLine().trim();

        Optional<User> auth = university.authenticate(login, password);
        if (auth.isPresent()) {
            currentUser = auth.get();
            System.out.printf(Lang.t("🎉 Добро пожаловать, %s (%s)%n",
                    "🎉 Welcome, %s (%s)%n",
                    "🎉 Қош келдіңіз, %s (%s)%n"),
                    currentUser.getFullName(), currentUser.getClass().getSimpleName());
        } else {
            System.out.println(Lang.t("❌ Неверный логин или пароль.",
                    "❌ Invalid login or password.",
                    "❌ Логин немесе құпия сөз қате."));
        }
    }

    private static void routeUserToMenu() {
        boolean logout = false;

        if (currentUser instanceof Admin) {
            logout = AdminMenuHandler.show((Admin) currentUser, scanner);
        } else if (currentUser instanceof Manager) {
            logout = ManagerMenuHandler.show((Manager) currentUser, scanner);
        } else if (currentUser instanceof Teacher) {
            logout = TeacherMenuHandler.show((Teacher) currentUser, scanner);
        } else if (currentUser instanceof GraduateStudent) {
            logout = GradStudentMenuHandler.show((GraduateStudent) currentUser, scanner);
        } else if (currentUser instanceof Student) {
            logout = StudentMenuHandler.show((Student) currentUser, scanner);
        } else if (currentUser instanceof TechSupportSpecialist) {
            logout = TechSupportMenuHandler.show((TechSupportSpecialist) currentUser, scanner);
        }

        if (logout) {
            currentUser = null;
        }
    }
}