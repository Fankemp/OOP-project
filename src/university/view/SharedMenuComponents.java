package university.view;

import university.storage.University;
import university.users.User;
import university.research.Researcher;
import university.research.ResearchManager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;
import university.enums.Language;

public class SharedMenuComponents {
    private static final University university = University.getInstance();

    public static void handleSendMessageMenu(User sender, Scanner scanner) {
        System.out.print(Lang.t("\nЛогин получателя: ", "\nReceiver login: ", "\nАлушының логині: "));
        String receiverLogin = scanner.nextLine().trim();
        User receiver = university.getUsers().stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(receiverLogin))
                .findFirst().orElse(null);
        if (receiver == null) {
            System.out.println(Lang.t("❌ Пользователь не найден.", "❌ User not found.", "❌ Пайдаланушы табылмады."));
            return;
        }
        System.out.print(Lang.t("Текст: ", "Text: ", "Мәтін: "));
        String text = scanner.nextLine().trim();
        if (text.isEmpty()) {
            System.out.println(Lang.t("⚠️ Пустое сообщение.", "⚠️ Empty message.", "⚠️ Бос хабар."));
            return;
        }
        university.sendMessage(sender, receiver, text);
        System.out.println(Lang.t("✅ Отправлено.", "✅ Sent.", "✅ Жіберілді."));
    }

    public static void handleViewMailboxMenu(User user, Scanner scanner) {
        System.out.println("\n=== " + Lang.t("ВХОДЯЩИЕ", "INBOX", "КІРІс ХАБАРЛАР") + " ===");
        var myMessages = university.getMessagesForUser(user);
        if (myMessages.isEmpty()) {
            System.out.println(Lang.t("Ящик пуст.", "Inbox is empty.", "Пошта жәшігі бос."));
            return;
        }
        for (int i = 0; i < myMessages.size(); i++) {
            var m = myMessages.get(i);
            String status = m.isRead()
                    ? Lang.t("[Прочитано]", "[Read]", "[Оқылды]")
                    : Lang.t("[НОВОЕ]", "[NEW]", "[ЖАҢА]");
            System.out.printf("%d. %s %s %s (%s)%n", i + 1, status,
                    Lang.t("от", "from", "жіберген"),
                    m.getSender().getFullName(), m.getSentAt());
        }
        System.out.print(Lang.t("Номер (Enter для отмены): ", "Number (Enter to cancel): ", "Нөмір (бас тарту үшін Enter): "));
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < myMessages.size()) {
                var msg = myMessages.get(index);
                msg.markAsRead();
                System.out.printf("%n%s %s%n%s %s%n%s %s%n",
                        Lang.t("От:", "From:", "Жіберген:"), msg.getSender().getFullName(),
                        Lang.t("Дата:", "Date:", "Күні:"), msg.getSentAt(),
                        Lang.t("Текст:", "Text:", "Мәтін:"), msg.getText());
            } else {
                System.out.println(Lang.t("❌ Неверный номер.", "❌ Invalid number.", "❌ Жарамсыз нөмір."));
            }
        } catch (Exception e) {
            System.out.println(Lang.t("⚠️ Отменено.", "⚠️ Cancelled.", "⚠️ Бас тартылды."));
        }
    }

    public static void handleSwitchLanguage(Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Смена языка", "Switch Language", "Тілді ауыстыру") + " ---");
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

    public static void handleResearcherMenu(Researcher researcher, Scanner scanner) {
        System.out.println("\n--- [" + Lang.t("Меню Исследователя", "Research Menu", "Зерттеуші мәзірі") + "] ---");
        System.out.println("1. " + Lang.t("Мои статьи", "My Papers", "Менің мақалаларым"));
        System.out.println("2. " + Lang.t("Опубликовать статью", "Add Paper", "Мақала жариялау"));
        System.out.println("3. " + Lang.t("h-index", "h-index", "h-индекс"));
        System.out.println("4. " + Lang.t("Назад", "Back", "Артқа"));
        System.out.print(Lang.t("Выберите действие: ", "Choose action: ", "Әрекетті таңдаңыз: "));

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                if (researcher.getPapers().isEmpty()) {
                    System.out.println(Lang.t("Статей нет.", "No papers.", "Мақалалар жоқ."));
                    return;
                }
                researcher.getPapers().forEach(System.out::println);
            }
            case "2" -> {
                try {
                    System.out.print(Lang.t("Название: ", "Title: ", "Атауы: "));
                    String title = scanner.nextLine().trim();
                    System.out.print(Lang.t("Журнал: ", "Journal: ", "Журнал: "));
                    String journal = scanner.nextLine().trim();
                    System.out.print(Lang.t("Цитирования: ", "Citations: ", "Цитаталар: "));
                    int citations = Integer.parseInt(scanner.nextLine().trim());
                    var paper = new university.research.ResearchPaper(
                            title, Arrays.asList("Author"), journal, 1, 10,
                            LocalDate.now(), "DOI-123", citations);
                    researcher.addPaper(paper);
                    ResearchManager.getInstance().registerResearcher(researcher);
                    System.out.println(Lang.t("✅ Статья добавлена.", "✅ Paper added.", "✅ Мақала қосылды."));
                } catch (Exception e) {
                    System.out.println(Lang.t("❌ Ошибка ввода.", "❌ Input error.", "❌ Енгізу қатесі."));
                }
            }
            case "3" -> System.out.println(Lang.t("Ваш h-index: ", "Your h-index: ", "Сіздің h-индексіңіз: ") + researcher.calculateHIndex());
            default -> System.out.println(Lang.t("Назад.", "Back.", "Артқа."));
        }
    }

    public static void handleViewNews() {
        System.out.println("\n=== " + Lang.t("ЛЕНТА НОВОСТЕЙ KBTU", "KBTU NEWS FEED", "KBTU ЖАҢАЛЫҚТАР ТАСПАСЫ") + " ===");
        java.util.List<university.communications.News> newsList = new java.util.ArrayList<>(university.getNews());
        if (newsList.isEmpty()) {
            System.out.println(Lang.t("Новостей нет.", "No news.", "Жаңалықтар жоқ."));
            return;
        }
        java.util.Collections.sort(newsList);
        System.out.println("-------------------------------------------------");
        newsList.forEach(System.out::println);
        System.out.println("-------------------------------------------------");
    }
}