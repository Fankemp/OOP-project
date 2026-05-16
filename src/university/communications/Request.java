package university.communications;

import university.users.User;
import university.enums.RequestStatus; // Импортируем Enum, который требует техподдержка

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


public class Request implements Serializable {
    private static final long serialVersionUID = 2026L;

    private String id;
    private User sender;
    private String description;

    private RequestStatus status;
    private LocalDateTime createdAt;

    public Request(User sender, String description) {
        this.sender = sender;
        this.description = description;
        this.status = RequestStatus.NEW;
        this.createdAt = LocalDateTime.now();

        this.id = "REQ-" + Math.abs(Objects.hash(sender, description, createdAt)) % 10000;
    }

    public String getId() { return id; }

    public RequestStatus getStatus() { return status; }

    public void updateStatus(RequestStatus newStatus) {
        if (newStatus == null) throw new IllegalArgumentException("Статус не может быть null");
        this.status = newStatus;
    }

    public User getSender() { return sender; }

    public String getDescription() { return description; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void view() {
        if (status == RequestStatus.NEW) {
            status = RequestStatus.VIEWED;
        }
    }

    public void accept() {
        if (status == RequestStatus.NEW) {
            view();
        }
        if (status == RequestStatus.VIEWED) {
            status = RequestStatus.ACCEPTED;
        }
    }

    public void reject() {
        if (status == RequestStatus.NEW) {
            view();
        }
        if (status == RequestStatus.VIEWED) {
            status = RequestStatus.REJECTED;
        }
    }

    public void markAsDone() {
        if (status == RequestStatus.ACCEPTED) {
            status = RequestStatus.DONE;
        }
    }

    @Override
    public String toString() {
        return String.format("Request{id='%s', sender=%s, description='%s', status=%s, date=%s}",
                id, sender.getFullName(), description, status, createdAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Request)) return false;
        Request request = (Request) obj;
        return Objects.equals(id, request.id) &&
                Objects.equals(sender, request.sender) &&
                Objects.equals(description, request.description) &&
                status == request.status &&
                Objects.equals(createdAt, request.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, description, status, createdAt);
    }
}