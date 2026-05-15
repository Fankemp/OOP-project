package university.communications;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class News implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String content;
    private String topic;
    private boolean pinned;
    private LocalDateTime createdAt;
    private List<Comment> comments;

    public News(String title, String content, String topic) {
        this.title = title;
        this.content = content;
        this.topic = topic;
        this.createdAt = LocalDateTime.now();
        this.comments = new ArrayList<>();

        if (topic != null && topic.equalsIgnoreCase("RESEARCH")) {
            this.pinned = true;
        } else {
            this.pinned = false;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTopic() {
        return topic;
    }

    public boolean isPinned() {
        return pinned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

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
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", topic='" + topic + '\'' +
                ", pinned=" + pinned +
                ", createdAt=" + createdAt +
                ", comments=" + comments +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof News)) return false;

        News news = (News) obj;

        return pinned == news.pinned &&
                Objects.equals(title, news.title) &&
                Objects.equals(content, news.content) &&
                Objects.equals(topic, news.topic) &&
                Objects.equals(createdAt, news.createdAt) &&
                Objects.equals(comments, news.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, topic, pinned, createdAt, comments);
    }
}