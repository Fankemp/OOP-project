package university.communications;

import university.users.User;
import university.enums.RequestStatus;

import java.io.Serializable;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;


public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;

    private final String id;
    private final User sender;
    private final String description;
    private RequestStatus status;
    private final LocalDateTime createdAt;

    private boolean signed = false;

    public Request(User sender, String description) {
        this.sender = Objects.requireNonNull(sender, "Sender cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.status = RequestStatus.NEW;
        this.createdAt = LocalDateTime.now();

        this.id = "REQ-" + Math.abs(Objects.hash(sender, description, createdAt)) % 10000;
    }

    public String getId() { return id; }
    public RequestStatus getStatus() { return status; }
    public User getSender() { return sender; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isSigned() { return signed; }
    public void sign() { this.signed = true; }

    public void updateStatus(RequestStatus newStatus) {
        if (newStatus == null) throw new IllegalArgumentException("Status cannot be null");
        this.status = newStatus;
    }

    // --- Логика стейт-машины переключения статусов ---
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request request)) return false;
        return Objects.equals(id, request.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String signMarker = signed ? "✍️ [SIGNED]" : "[UNSIGNED]";
        return String.format("Request %s {id='%s', sender=%s, status=%s, desc='%s', date=%s}",
                signMarker, id, sender.getFullName(), status, description, createdAt);
    }
}