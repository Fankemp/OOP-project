package university.view;

import university.storage.University;
import university.users.TechSupportSpecialist;
import university.users.User;
import university.communications.Request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class TechSupportMenuHandler {
    private static final University university = University.getInstance();

    public static boolean show(TechSupportSpecialist techSupport, Scanner scanner) {
        System.out.println("\n--- [Панель Технической Поддержки KBTU] ---");
        System.out.println("1. Проверить системный журнал логов (View System Logs)");
        System.out.println("2. Просмотреть входящие заявки на ремонт (Requests)");
        System.out.println("3. Создать новую техническую заявку (New Request)");
        System.out.println("4. Выйти из системы (Logout)");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> printLogsFromFile();
            case "2" -> handleRequestsMenu(scanner);
            case "3" -> handleCreateRequest(techSupport, scanner);
            case "4" -> {
                System.out.println("Сессия техподдержки завершена.");
                return true; // Сигнал логаута для диспетчера
            }
            default -> System.out.println("⚠️ Ошибка: Неверный выбор пункта меню.");
        }
        return false;
    }

    public static void handleCreateRequest(User sender, Scanner scanner) {
        System.out.println("\n--- Создание новой заявки на ремонт ---");
        System.out.print("Опишите техническую проблему (например, сломался проектор): ");
        String description = scanner.nextLine().trim();

        if (description.isEmpty()) {
            System.out.println("❌ Ошибка: Описание проблемы не может быть пустым.");
            return;
        }

        Request request = new Request(sender, description);
        university.addRequest(request);
        System.out.println("✅ Заявка успешно зарегистрирована. ID заявки: " + request.getId());
    }

    private static void handleRequestsMenu(Scanner scanner) {
        System.out.println("\n=== ОЧЕРЕДЬ ЗАЯВЛЕНИЙ ТЕХПОДДЕРЖКИ ===");
        var requests = university.getRequests();

        if (requests == null || requests.isEmpty()) {
            System.out.println("Активных заявок на ремонт в системе нет.");
            return;
        }

        // При просмотре переводим статус из NEW в VIEWED
        requests.forEach(r -> {
            r.view();
            System.out.println(r);
        });

        System.out.print("\nВведите ID заявки для управления (или нажмите Enter для отмены): ");
        String reqId = scanner.nextLine().trim();
        if (reqId.isEmpty()) return;

        Request targetRequest = requests.stream()
                .filter(r -> r.getId().equalsIgnoreCase(reqId))
                .findFirst().orElse(null);

        if (targetRequest == null) {
            System.out.println("❌ Ошибка: Заявка с таким идентификатором не найдена.");
            // Мелкое исправление: убран некорректный return, ломавший цикл меню
            return;
        }

        System.out.println("\nВыберите решение для заявки " + targetRequest.getId() + ":");
        System.out.println("1. Принять в работу (ACCEPT)");
        System.out.println("2. Отклонить заявление (REJECT)");
        System.out.println("3. Отметить как выполненную (DONE)");
        System.out.print("Ваш выбор: ");

        String act = scanner.nextLine().trim();
        if (act.equals("1")) targetRequest.accept();
        else if (act.equals("2")) targetRequest.reject();
        else if (act.equals("3")) targetRequest.markAsDone();

        System.out.println("✅ Статус технической заявки успешно обновлен.");
    }

    private static void printLogsFromFile() {
        File logFile = new File("university.log");
        if (!logFile.exists() || logFile.length() == 0) {
            System.out.println("Журнал системного аудита пуст.");
            return;
        }
        System.out.println("\n=== ВЫЧИТКА ЛОГ-ФАЙЛА (UNIVERSITY.LOG) ===");
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении лог-файла: " + e.getMessage());
        }
        System.out.println("============================================");
    }
}