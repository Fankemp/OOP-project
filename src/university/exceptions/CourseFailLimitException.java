    package university.exceptions;

import java.io.Serial;

public class CourseFailLimitException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2026L;

    public CourseFailLimitException(String message) {
        super(message);
    }
}