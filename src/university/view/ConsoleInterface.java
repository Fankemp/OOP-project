package university.view;

import university.storage.University;
import university.users.*;
import java.util.Optional;
import java.util.Scanner;


public class ConsoleInterface {
    private static final University university = University.getInstance();
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void start() {
        while (true) {
            // Если пользователь уже вошел в систему — сразу перенаправляем в его личный кабинет
            if (currentUser != null) {
                routeUserToMenu();
                continue;
            }

            System.out.println("\n========================================");
            System.out.println("     Добро пожаловать в систему KBTU    ");
            System.out.println("========================================");
            System.out.println("1. Войти в систему");
            System.out.println("2. Выйти из программы");
            System.out.print("Выберите действие: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> showLoginMenu();
                case "2" -> {
                    System.out.println("\n[Система]: Фоновая синхронизация и сохранение данных...");
                    university.save(); // Автоматическое сохранение изменений при выходе
                    System.out.println("Все изменения успешно сохранены. До встречи!");
                    return; // Завершение работы бесконечного цикла и программы
                }
                default -> System.out.println("❌ Ошибка: Неверный выбор пункта меню. Попробуйте еще раз.");
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n--- [ Форма авторизации пользователя ] ---");
        System.out.print("Введите ваш логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Введите ваш пароль: ");
        String password = scanner.nextLine().trim();

        Optional<User> auth = university.authenticate(login, password);
        if (auth.isPresent()) {
            currentUser = auth.get();
            System.out.printf("🎉 Авторизация успешна! Добро пожаловать, %s (Роль: %s)%n",
                    currentUser.getFullName(), currentUser.getClass().getSimpleName());
        } else {
            System.out.println("❌ Ошибка: Неверный логин или пароль. Доступ отклонен.");
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

        // Если из обработчика роли вернулся true — сбрасываем текущую сессию
        if (logout) {
            currentUser = null;
        }
    }
}