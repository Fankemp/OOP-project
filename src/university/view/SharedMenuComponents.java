package university.view;

import university.storage.University;
import university.users.User;
import university.research.Researcher;
import university.research.ResearchManager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Класс сквозных переиспользуемых компонентов интерфейса (Shared Components).
 * Устраняет дублирование кода (DRY) для общих процессов: почты, локализации и науки.
 */
public class SharedMenuComponents {
    private static final University university = University.getInstance();

    /**
     * Универсальный текстовый интерфейс для отправки личного сообщения.
     */
    public static void handleSendMessageMenu(User sender, Scanner scanner) {
        System.out.print("\nВведите логин получателя: ");
        String receiverLogin = scanner.nextLine().trim();

        User receiver = university.getUsers().stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(receiverLogin))
                .findFirst().orElse(null);

        if (receiver == null) {
            System.out.println("❌ Ошибка: Пользователь с таким логином не найден.");
            return;
        }

        System.out.print("Введите текст сообщения: ");
        String text = scanner.nextLine().trim();

        if (text.isEmpty()) {
            System.out.println("⚠️ Действие отменено: нельзя отправить пустое сообщение.");
            return;
        }

        university.sendMessage(sender, receiver, text);
        System.out.println("✅ Сообщение успешно отправлено получателю.");
    }

    /**
     * Универсальный интерфейс вычитки писем из почтового ящика (Mailbox).
     */
    public static void handleViewMailboxMenu(User user, Scanner scanner) {
        System.out.println("\n=== Входящие сообщения ===");
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

        System.out.print("Введите номер сообщения для чтения (или Enter для отмены): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;

        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < myMessages.size()) {
                var selectedMsg = myMessages.get(index);
                selectedMsg.markAsRead(); // Смена флага на чтение

                System.out.printf("%n--- Текст письма ---%nОт: %s%nТекст: %s%n--------------------%n",
                        selectedMsg.getSender().getFullName(), selectedMsg.getText());
            } else {
                System.out.println("❌ Ошибка: Некорректный номер сообщения.");
            }
        } catch (Exception e) {
            System.out.println("❌ Ошибка: Неверный формат ввода.");
        }
    }

    /**
     * Меню динамического переключения языкового пакета системы.
     */
    public static void handleSwitchLanguage(Scanner scanner) {
        System.out.println("\n--- Настройка локализации / Switch Language ---");
        System.out.println("1. English (EN) | 2. Казахский (KZ) | 3. Русский (RU)");
        System.out.print("Ваш выбор: ");
        String choice = scanner.nextLine().trim();

        String lang = choice.equals("2") ? "KZ" : choice.equals("3") ? "RU" : "EN";
        System.out.println("Language successfully switched to: " + lang);
    }

    /**
     * Общая подсистема вызовов для академических исследователей (Researcher).
     * Автоматически принимает любого Researcher (и Teacher, и GraduateStudent).
     */
    public static void handleResearcherMenu(Researcher researcher, Scanner scanner) {
        System.out.println("\n--- [Меню Исследователя KBTU] ---");
        System.out.println("1. Мои научные статьи");
        System.out.println("2. Опубликовать новую статью (Add Paper)");
        System.out.println("3. Рассчитать индекс Хирша (h-index)");
        System.out.println("4. Вернуться назад");
        System.out.print("Выберите действие: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                if (researcher.getPapers().isEmpty()) {
                    System.out.println("В вашем научном профиле пока нет зарегистрированных статей.");
                    return;
                }
                researcher.getPapers().forEach(System.out::println);
            }
            case "2" -> {
                try {
                    System.out.print("Title (Название): ");
                    String title = scanner.nextLine().trim();
                    System.out.print("Journal (Издательство): ");
                    String journal = scanner.nextLine().trim();
                    System.out.print("Citations (Цитирования): ");
                    int citations = Integer.parseInt(scanner.nextLine().trim());

                    // Формируем объект статьи и привязываем его к репозиториям
                    var paper = new university.research.ResearchPaper(
                            title, Arrays.asList("Author"), journal, 1, 10, LocalDate.now(), "DOI-123", citations);

                    researcher.addPaper(paper);
                    ResearchManager.getInstance().registerResearcher(researcher);
                    System.out.println("✅ Статья добавлена. Научный авто-анонс опубликован в ленте новостей.");
                } catch (Exception e) {
                    System.out.println("❌ Ошибка ввода: некорректный формат числовых данных.");
                }
            }
            case "3" -> {
                System.out.println("Ваш индекс Хирша (h-index): " + researcher.calculateHIndex());
            }
            default -> System.out.println("Возврат в основное кабинет-меню.");
        }
    }

    /**
     * Универсальный метод просмотра новостной ленты вуза для всех ролей.
     * Автоматически сортирует новости по приоритету (Comparable).
     */
    public static void handleViewNews() {
        System.out.println("\n=== ЛЕНТА НОВОСТЕЙ KBTU ===");
        java.util.List<university.communications.News> newsList = new java.util.ArrayList<>(university.getNews());

        if (newsList.isEmpty()) {
            System.out.println("Новостная лента университета пока пуста.");
            return;
        }

        // Сортировка (благодаря реализованному Comparable важные и научные новости будут выше)
        java.util.Collections.sort(newsList);

        System.out.println("-------------------------------------------------");
        newsList.forEach(System.out::println);
        System.out.println("-------------------------------------------------");
    }
}