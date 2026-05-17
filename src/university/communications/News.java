package university.communications;

import java.io.Serializable;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class News implements Serializable, Comparable<News> {
    @Serial
    private static final long serialVersionUID = 2026L; // Синхронизируем версию с остальным проектом

    private final String title;
    private final String content;
    private final String topic;
    private boolean pinned;
    private final LocalDateTime createdAt;
    private final List<Comment> comments;

    public News(String title, String content, String topic) {
        this.title = title;
        this.content = content;
        this.topic = topic;
        this.createdAt = LocalDateTime.now();
        this.comments = new ArrayList<>();

        // Автоматически закрепляем научные публикации по ТЗ
        if (topic != null && topic.equalsIgnoreCase("RESEARCH")) {
            this.pinned = true;
        } else {
            this.pinned = false;
        }
    }


    @Override
    public int compareTo(News other) {
        if (this.pinned && !other.pinned) return -1; // Эта новость выше
        if (!this.pinned && other.pinned) return 1;  // Эта новость ниже

        return other.createdAt.compareTo(this.createdAt);
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTopic() { return topic; }
    public boolean isPinned() { return pinned; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void addComment(Comment comment) {
        if (comment != null) {
            comments.add(comment);
        }
    }

    public void pin() {
        this.pinned = true;
    }

    public void unpin() {
        if (topic != null && topic.equalsIgnoreCase("RESEARCH")) {
            return;
        }
        this.pinned = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News news)) return false;
        return pinned == news.pinned &&
                Objects.equals(title, news.title) &&
                Objects.equals(topic, news.topic) &&
                Objects.equals(createdAt, news.createdAt);
    }

    @Override
    public int hashCode() {
        // ИСПРАВЛЕНО: Убрали изменяемый список comments, чтобы объект не терялся в коллекциях
        return Objects.hash(title, topic, pinned, createdAt);
    }

    @Override
    public String toString() {
        String marker = pinned ? "📌 [PINNED]" : "";
        return String.format("%s [%s] %s (%s)%n  %s%n  Комментариев: %d",
                marker, topic.toUpperCase(), title, createdAt, content, comments.size());
    }
}