package university.communications;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Journal implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Subscriber> subscribers;
    private List<String> announcements;
    private LocalDateTime createdAt;

    public Journal(String name) {
        this.name = name;
        this.subscribers = new ArrayList<>();
        this.announcements = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Subscriber> getSubscribers() {
        return Collections.unmodifiableList(subscribers);
    }

    public List<String> getAnnouncements() {
        return Collections.unmodifiableList(announcements);
    }

    public void subscribe(Subscriber subscriber) {
        if (subscriber != null && !subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void publishAnnouncement(String announcement) {
        if (announcement == null || announcement.trim().isEmpty()) {
            return;
        }

        announcements.add(announcement);
        notifySubscribers(announcement);
    }

    private void notifySubscribers(String message) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update("Journal [" + name + "]: " + message);
        }
    }

    @Override
    public String toString() {
        return "Journal{" +
                "name='" + name + '\'' +
                ", subscribersCount=" + subscribers.size() +
                ", announcements=" + announcements +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Journal)) return false;

        Journal journal = (Journal) obj;

        return Objects.equals(name, journal.name) &&
                Objects.equals(createdAt, journal.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, createdAt);
    }
}