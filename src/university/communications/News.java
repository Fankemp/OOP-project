package university.communications;

import java.io.Serializable;
import java.io.Serial;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class News implements Serializable, Comparable<News> {
    @Serial
    private static final long serialVersionUID = 2026L;

    private final String title;
    private final String content;
    private final String topic;
    private final LocalDateTime createdAt;

    public News(String title, String content, String topic) {
        this.title = title;
        this.content = content;
        this.topic = topic;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public int compareTo(News other) {
        // просто сортируем по дате сначала самые свежие новости
        return other.createdAt.compareTo(this.createdAt);
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTopic() { return topic; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News news)) return false;
        return Objects.equals(title, news.title) &&
                Objects.equals(topic, news.topic) &&
                Objects.equals(createdAt, news.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, topic, createdAt);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = (createdAt != null) ? createdAt.format(formatter) : "Нет даты";

        return String.format("[%s] %s (%s)%n  %s",
                topic.toUpperCase(), title, formattedDate, content);
    }
}