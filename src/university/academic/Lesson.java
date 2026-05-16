package university.academic;

import university.enums.LessonType;
import java.io.Serializable;
import java.io.Serial;
import java.util.Objects;

/**
 * Класс, описывающий академическое занятие в университете.
 * Демонстрирует принципы инкапсуляции данных.
 */
public class Lesson implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;

    private final LessonType lessonType;
    private final String room;
    private final String timeslot; // Формат: "Пн 10:00-11:00"

    public Lesson(LessonType lessonType, String room, String timeslot) {
        this.lessonType = Objects.requireNonNull(lessonType, "Тип занятия не может быть null");
        this.room = Objects.requireNonNull(room, "Аудитория не может быть null");
        this.timeslot = Objects.requireNonNull(timeslot, "Временной слот не может быть null");
    }

    public LessonType getLessonType() { return lessonType; }
    public String getRoom() { return room; }
    public String getTimeslot() { return timeslot; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return lessonType == lesson.lessonType &&
                room.equals(lesson.room) &&
                timeslot.equals(lesson.timeslot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessonType, room, timeslot);
    }

    @Override
    public String toString() {
        return String.format("[%s] Ауд: %s, Время: %s", lessonType, room, timeslot);
    }
}