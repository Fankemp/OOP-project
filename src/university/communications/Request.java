package university.communications;

import university.user.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String NEW = "NEW";
    public static final String VIEWED = "VIEWED";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";
    public static final String DONE = "DONE";

    private User sender;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    public Request(User sender, String description) {
        this.sender = sender;
        this.description = description;
        this.status = NEW;
        this.createdAt = LocalDateTime.now();
    }

    public User getSender() {
        return sender;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void view() {
        if (status.equals(NEW)) {
            status = VIEWED;
        }
    }

    public void accept() {
        if (status.equals(NEW)) {
            view();
        }

        if (status.equals(VIEWED)) {
            status = ACCEPTED;
        }
    }

    public void reject() {
        if (status.equals(NEW)) {
            view();
        }

        if (status.equals(VIEWED)) {
            status = REJECTED;
        }
    }

    public void markAsDone() {
        if (status.equals(ACCEPTED)) {
            status = DONE;
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "sender=" + sender +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Request)) return false;

        Request request = (Request) obj;

        return Objects.equals(sender, request.sender) &&
                Objects.equals(description, request.description) &&
                Objects.equals(status, request.status) &&
                Objects.equals(createdAt, request.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, description, status, createdAt);
    }
}