package university.view;

import university.storage.University;
import university.users.User;
import java.util.Scanner;


public class CommunicationHandler {
    private static final University university = University.getInstance();

    public static void handleSendMessageMenu(User sender, Scanner scanner) {
        System.out.println("\n--- Отправка личного сообщения ---");
        System.out.print("Введите логин получателя (admin, manager, teacher, student, tech): ");
        String receiverLogin = scanner.nextLine().trim();

        User receiver = university.getUsers().stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(receiverLogin))
                .findFirst().orElse(null);

        if (receiver == null) {
            System.out.println("❌ Ошибка: Пользователь с таким логином не найден.");
            return;
        }

        if (receiver.equals(sender)) {
            System.out.println("❌ Ошибка: Нельзя отправлять сообщения самому себе.");
            return;
        }

        System.out.print("Введите текст сообщения: ");
        String text = scanner.nextLine().trim();

        if (text.isEmpty()) {
            System.out.println("⚠️ Отмена: Нельзя отправить пустое сообщение.");
            return;
        }

        university.sendMessage(sender, receiver, text);
        System.out.println("✅ Сообщение успешно доставлено пользователю " + receiver.getFullName());
    }

    /**
     * Универсальное меню просмотра входящего почтового ящика.
     */
    public static void handleViewMailboxMenu(User user, Scanner scanner) {
        System.out.println("\n=== ВХОДЯЩИЕ СООБЩЕНИЯ (ПОЧТОВЫЙ ЯЩИК) ===");
        var myMessages = university.getMessagesForUser(user);

        if (myMessages.isEmpty()) {
            System.out.println("Ваш электронный ящик пуст.");
            return;
        }

        for (int i = 0; i < myMessages.size(); i++) {
            var m = myMessages.get(i);
            String status = m.isRead() ? "[Прочитано]" : "[📌 НОВОЕ]";
            System.out.printf("%d. %s от %s (%s)%n", i + 1, status, m.getSender().getFullName(), m.getSentAt());
        }

        System.out.println("-------------------------------------------------");
        System.out.print("Введите номер письма для чтения (или Enter для отмены): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;

        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < myMessages.size()) {
                var selectedMsg = myMessages.get(index);
                selectedMsg.markAsRead(); // Автоматически помечаем прочитанным

                System.out.println("\n-----------------------------------------");
                System.out.println("ОТПРАВИТЕЛЬ: " + selectedMsg.getSender().getFullName());
                System.out.println("ДАТА:        " + selectedMsg.getSentAt());
                System.out.println("СОДЕРЖАНИЕ:  " + selectedMsg.getText());
                System.out.println("-----------------------------------------");
            } else {
                System.out.println("❌ Ошибка: Неверный номер сообщения.");
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Действие отменено.");
        }
    }
}