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
        System.out.println("\n--- [" + Lang.t("Панель Техподдержки", "Tech Support Panel", "Техқолдау панелі") + "] ---");
        System.out.println("1. " + Lang.t("Просмотреть логи", "View Logs", "Журналды көру"));
        System.out.println("2. " + Lang.t("Входящие заявки", "View Requests", "Кіріс өтінімдер"));
        System.out.println("3. " + Lang.t("Создать заявку", "New Request", "Өтініш жасау"));
        System.out.println("4. " + Lang.t("Выйти", "Logout", "Шығу"));
        System.out.println("5. " + Lang.t("Сменить язык", "Switch Language", "Тілді ауыстыру"));
        System.out.print(Lang.t("Выберите действие: ", "Choose action: ", "Әрекетті таңдаңыз: "));

        switch (scanner.nextLine().trim()) {
            case "1" -> printLogsFromFile();
            case "2" -> handleRequestsMenu(scanner);
            case "3" -> handleCreateRequest(techSupport, scanner);
            case "4" -> {
                System.out.println(Lang.t("Сессия завершена.", "Session ended.", "Сессия аяқталды."));
                return true;
            }
            case "5" -> SharedMenuComponents.handleSwitchLanguage(scanner);
            default -> System.out.println(Lang.t("⚠️ Неверный выбор.", "⚠️ Invalid choice.", "⚠️ Жарамсыз таңдау."));
        }
        return false;
    }

    public static void handleCreateRequest(User sender, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Создание заявки", "New Request", "Өтініш жасау") + " ---");
        System.out.print(Lang.t("Опишите проблему: ", "Describe the problem: ", "Мәселені сипаттаңыз: "));
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) {
            System.out.println(Lang.t("❌ Описание не может быть пустым.", "❌ Description required.", "❌ Сипаттама бос болмауы керек."));
            return;
        }
        Request request = new Request(sender, description);
        university.addRequest(request);
        System.out.println(Lang.t("✅ Заявка зарегистрирована. ID: ", "✅ Request created. ID: ", "✅ Өтініш тіркелді. ID: ") + request.getId());
    }

    private static void handleRequestsMenu(Scanner scanner) {
        System.out.println("\n=== " + Lang.t("ОЧЕРЕДЬ ЗАЯВОК", "REQUEST QUEUE", "ӨТІНІМДЕР КЕЗЕГІ") + " ===");
        var requests = university.getRequests();
        if (requests == null || requests.isEmpty()) {
            System.out.println(Lang.t("Заявок нет.", "No requests.", "Өтінімдер жоқ."));
            return;
        }
        requests.forEach(r -> { r.view(); System.out.println(r); });

        System.out.print(Lang.t("ID заявки (Enter для отмены): ", "Request ID (Enter to cancel): ", "Өтініш ID (бас тарту үшін Enter): "));
        String reqId = scanner.nextLine().trim();
        if (reqId.isEmpty()) return;

        Request target = requests.stream()
                .filter(r -> r.getId().equalsIgnoreCase(reqId))
                .findFirst().orElse(null);
        if (target == null) {
            System.out.println(Lang.t("❌ Не найдена.", "❌ Not found.", "❌ Табылмады."));
            return;
        }

        System.out.println("1. " + Lang.t("Принять (ACCEPT)", "Accept", "Қабылдау"));
        System.out.println("2. " + Lang.t("Отклонить (REJECT)", "Reject", "Қабылдамау"));
        System.out.println("3. " + Lang.t("Выполнено (DONE)", "Done", "Орындалды"));
        System.out.print(Lang.t("Ваш выбор: ", "Your choice: ", "Таңдауыңыз: "));

        switch (scanner.nextLine().trim()) {
            case "1" -> target.accept();
            case "2" -> target.reject();
            case "3" -> target.markAsDone();
        }
        System.out.println(Lang.t("✅ Статус обновлён.", "✅ Status updated.", "✅ Мәртебе жаңартылды."));
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
            System.out.println(Lang.t("Ошибка чтения: ", "Read error: ", "Оқу қатесі: ") + e.getMessage());
        }
        System.out.println("==========================================");
    }
}