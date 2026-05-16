package university.academic;

import java.io.Serializable;
import java.io.Serial;
import java.util.*;

/**
 * Класс представляет академический транскрипт студента.
 * Использует Java Streams API для расчета средневзвешенного GPA (Лекция №15).
 */
public class Transcript implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;

    // Связка Курс -> Оценка. Использован LinkedHashMap для сохранения порядка добавления предметов
    private final Map<Course, Mark> records = new LinkedHashMap<>();

    public Transcript() {}

    /**
     * Добавляет запись об оценке за курс в транскрипт.
     */
    public void addRecord(Course course, Mark mark) {
        Objects.requireNonNull(course, "Курс не может быть null");
        Objects.requireNonNull(mark, "Оценка не может быть null");
        this.records.put(course, mark);
    }

    /**
     * Вычисляет текущий GPA студента.
     * Реализовано через Streams API (Лекция №15) по формуле взвешенного среднего.
     */
    public double calculateGpa() {
        if (records.isEmpty()) return 0.0;

        // Сумма (GPA поинты за курс * Кредиты курса)
        double totalWeightedPoints = records.entrySet().stream()
                .mapToDouble(entry -> entry.getValue().getGpaPoints() * entry.getKey().getCredits())
                .sum();

        // Сумма всех кредитов
        int totalCredits = records.keySet().stream()
                .mapToInt(Course::getCredits)
                .sum();

        return totalCredits == 0 ? 0.0 : totalWeightedPoints / totalCredits;
    }

    public Map<Course, Mark> getRecords() {
        return Collections.unmodifiableMap(records);
    }

    @Override
    public String toString() {
        if (records.isEmpty()) return "Транскрипт пуст.";

        StringBuilder sb = new StringBuilder("========== ОФИЦИАЛЬНЫЙ ТРАНСКРИПТ ==========\n");
        records.forEach((course, mark) ->
                sb.append(String.format("%s: %-20s | Оценка: %-5s | %s\n",
                        course.getCode(), course.getName(), mark.getLetterGrade(), mark.toString()))
        );
        sb.append("--------------------------------------------\n");
        sb.append(String.format("Итоговый GPA системы: %.2f\n", calculateGpa()));
        sb.append("============================================");
        return sb.toString();
    }
}