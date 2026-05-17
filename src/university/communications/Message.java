package university.communications;

import university.users.User;

import java.io.Serializable;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс внутреннего сообщения системы.
 * ИСПРАВЛЕНО: Поля сделаны неизменяемыми, убран баг с динамическим hashCode.
 */
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;

    private final User sender;
    private final User receiver;
    private final String text;
    private final LocalDateTime sentAt;
    private boolean read;

    public Message(User sender, User receiver, String text) {
        this.sender = Objects.requireNonNull(sender, "Sender cannot be null");
        this.receiver = Objects.requireNonNull(receiver, "Receiver cannot be null");
        this.text = Objects.requireNonNull(text, "Message text cannot be null");
        this.sentAt = LocalDateTime.now();
        this.read = false;
    }

    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public String getText() { return text; }
    public LocalDateTime getSentAt() { return sentAt; }
    public boolean isRead() { return read; }

    public void markAsRead() {
        this.read = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return Objects.equals(sender, message.sender) &&
                Objects.equals(receiver, message.receiver) &&
                Objects.equals(text, message.text) &&
                Objects.equals(sentAt, message.sentAt);
    }

    @Override
    public int hashCode() {
        // Хэш стабилен, даже если сообщение прочитают
        return Objects.hash(sender, receiver, text, sentAt);
    }

    @Override
    public String toString() {
        String status = read ? "Прочитано" : "✉️ НОВОЕ";
        return String.format("[%s] От: %s | Получено: %s%n   Текст: %s",
                status, sender.getFullName(), sentAt, text);
    }
}