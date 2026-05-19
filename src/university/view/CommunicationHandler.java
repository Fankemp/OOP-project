package university.view;

import university.storage.University;
import university.users.User;
import java.util.Scanner;

public class CommunicationHandler {
    private static final University university = University.getInstance();

    public static void handleSendMessageMenu(User sender, Scanner scanner) {
        System.out.println("\n--- " + Lang.t("Отправка сообщения", "Send Message", "Хабар жіберу") + " ---");
        System.out.print(Lang.t("Логин получателя: ", "Receiver login: ", "Алушының логині: "));
        String receiverLogin = scanner.nextLine().trim();
        User receiver = university.getUsers().stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(receiverLogin))
                .findFirst().orElse(null);
        if (receiver == null) {
            System.out.println(Lang.t("❌ Пользователь не найден.", "❌ User not found.", "❌ Пайдаланушы табылмады."));
            return;
        }
        if (receiver.equals(sender)) {
            System.out.println(Lang.t("❌ Нельзя писать себе.", "❌ Cannot message yourself.", "❌ Өзіңізге жаза алмайсыз."));
            return;
        }
        System.out.print(Lang.t("Текст сообщения: ", "Message text: ", "Хабар мәтіні: "));
        String text = scanner.nextLine().trim();
        if (text.isEmpty()) {
            System.out.println(Lang.t("⚠️ Пустое сообщение.", "⚠️ Empty message.", "⚠️ Бос хабар."));
            return;
        }
        university.sendMessage(sender, receiver, text);
        System.out.println(Lang.t("✅ Доставлено: ", "✅ Delivered to: ", "✅ Жеткізілді: ") + receiver.getFullName());
    }

    public static void handleViewMailboxMenu(User user, Scanner scanner) {
        System.out.println("\n=== " + Lang.t("ВХОДЯЩИЕ СООБЩЕНИЯ", "INBOX", "КІРІс ХАБАРЛАР") + " ===");
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
        System.out.print(Lang.t("Номер письма (Enter для отмены): ", "Message number (Enter to cancel): ", "Хабар нөмірі (бас тарту үшін Enter): "));
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < myMessages.size()) {
                var msg = myMessages.get(index);
                msg.markAsRead();
                System.out.println("\n-----------------------------------------");
                System.out.println(Lang.t("ОТ: ", "FROM: ", "ЖІБЕРГЕН: ") + msg.getSender().getFullName());
                System.out.println(Lang.t("ДАТА: ", "DATE: ", "КҮНІ: ") + msg.getSentAt());
                System.out.println(Lang.t("ТЕКСТ: ", "TEXT: ", "МӘТІН: ") + msg.getText());
                System.out.println("-----------------------------------------");
            } else {
                System.out.println(Lang.t("❌ Неверный номер.", "❌ Invalid number.", "❌ Жарамсыз нөмір."));
            }
        } catch (NumberFormatException e) {
            System.out.println(Lang.t("⚠️ Отменено.", "⚠️ Cancelled.", "⚠️ Бас тартылды."));
        }
    }
}