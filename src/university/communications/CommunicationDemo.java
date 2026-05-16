package university.communications;

public class CommunicationDemo {
    public static void main(String[] args) {
        System.out.println("===== COMMUNICATION MODULE DEMO =====");

        // 1. Message test
        Message message = new Message(null, null, "Hello, this is a test message.");
        System.out.println("\n--- Message before reading ---");
        System.out.println(message);

        message.markAsRead();
        System.out.println("\n--- Message after reading ---");
        System.out.println(message);

        // 2. News and Comment test
        News news = new News(
                "Research Paper Published",
                "A new research paper has been published by university researchers.",
                "RESEARCH"
        );

        Comment comment = new Comment(null, "Great news!");
        news.addComment(comment);

        System.out.println("\n--- News test ---");
        System.out.println(news);
        System.out.println("Is pinned: " + news.isPinned());

        // 3. Request test
        Request request = new Request(null, "Projector is not working in room 302.");

        System.out.println("\n--- Request created ---");
        System.out.println(request);

        request.view();
        System.out.println("\n--- Request viewed ---");
        System.out.println(request);

        request.accept();
        System.out.println("\n--- Request accepted ---");
        System.out.println(request);

        request.markAsDone();
        System.out.println("\n--- Request done ---");
        System.out.println(request);

        // 4. Journal + Subscriber test
        Journal journal = new Journal("University Research Journal");

        Subscriber subscriber = new Subscriber() {
            @Override
            public void update(String message) {
                System.out.println("Subscriber received notification: " + message);
            }
        };

        journal.subscribe(subscriber);

        System.out.println("\n--- Journal test ---");
        journal.publishAnnouncement("New research article is available.");

        System.out.println("\n===== DEMO FINISHED =====");
    }
}