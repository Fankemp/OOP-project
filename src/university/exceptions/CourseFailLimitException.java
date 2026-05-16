package university.exceptions;

public class CourseFailLimitException extends Exception {

    private static final int MAX_FAILS = 3;

    public CourseFailLimitException(String studentName, String courseName) {
        super(String.format(
                "Student '%s' has reached the maximum of %d failures for course '%s' and cannot retake it.",
                studentName, MAX_FAILS, courseName
        ));
    }
}