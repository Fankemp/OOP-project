package university.academic;

import university.storage.University;
import university.users.Student;

import java.util.Map;
import java.util.stream.Collectors;


public class AcademicAnalytics {
    public static String generateCoursesPerformanceReport() {
        University university = University.getInstance();

        if (university.getCourses().isEmpty()) {
            return "--- Отчет пуст: в системе нет зарегистрированных курсов ---";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("      АКАДЕМИЧЕСКИЙ ОТЧЕТ УСПЕВАЕМОСТИ УНИВЕРСИТЕТА\n");
        sb.append("==================================================\n");

        university.getUsers().stream()
                .filter(user -> user instanceof Student)
                .map(user -> (Student) user)
                .flatMap(student -> student.getMarks().entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey, // Группируем по Курсу
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList()) // Собираем все Mark для этого курса
                ))
                .forEach((course, marks) -> {
                    // Считаем средний балл по курсу через Stream API
                    double averageScore = marks.stream()
                            .mapToDouble(Mark::getTotal)
                            .average()
                            .orElse(0.0);

                    // Считаем количество заваливших (F)
                    long failedCount = marks.stream()
                            .filter(Mark::isFailed)
                            .count();

                    double passRate = marks.isEmpty() ? 0.0 : ((double) (marks.size() - failedCount) / marks.size()) * 100;

                    sb.append(String.format("Курс: %s [%s]\n", course.getName(), course.getCode()));
                    sb.append(String.format("  - Всего студентов записано: %d\n", marks.size()));
                    sb.append(String.format("  - Средний балл по курсу:   %.2f / 100.0\n", averageScore));
                    sb.append(String.format("  - Процент успеваемости:    %.1f%%\n", passRate));
                    sb.append("--------------------------------------------------\n");
                });

        return sb.toString();
    }
}