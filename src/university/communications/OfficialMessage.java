package university.communications;

import university.users.User;
import java.io.Serializable;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

public class OfficialMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;

    private final String id;
    private final String subject;
    private final String body;
    private final User sender;
    private final LocalDateTime createdAt;

    public OfficialMessage(String id, String subject, String body, User sender) {
        this.id = Objects.requireNonNull(id);
        this.subject = Objects.requireNonNull(subject);
        this.body = Objects.requireNonNull(body);
        this.sender = Objects.requireNonNull(sender);
        this.createdAt = LocalDateTime.now();
    }

    public String getId()           { return id; }
    public String getSubject()      { return subject; }
    public String getBody()         { return body; }
    public User getSender()         { return sender; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("[Официальное сообщение] %s\nОт: %s\nДата: %s\n%s",
                subject, sender.getFullName(), createdAt, body);
    }
}