package university.communications;

import university.users.User;

import java.io.Serializable;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;


public class Comment implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;

    private final User author;
    private final String text;
    private final LocalDateTime createdAt;

    public Comment(User author, String text) {
        this.author = Objects.requireNonNull(author, "Author cannot be null");

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }
        this.text = text.trim();
        this.createdAt = LocalDateTime.now();
    }

    public User getAuthor() { return author; }
    public String getText() { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return Objects.equals(author, comment.author) &&
                Objects.equals(text, comment.text) &&
                Objects.equals(createdAt, comment.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, text, createdAt);
    }


    @Override
    public String toString() {
        String authorName = (author != null) ? author.getFullName() : "Anonymous";
        return String.format("    ↳ [%s] %s: \"%s\"", createdAt, authorName, text);
    }
}