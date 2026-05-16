package university.exceptions;

public class MaxCreditsException extends Exception {

    private static final int MAX_CREDITS = 21;

    public MaxCreditsException(int currentCredits, int courseCredits) {
        super(String.format(
                "Cannot register: adding %d credit(s) to current %d would exceed the %d-credit limit.",
                courseCredits, currentCredits, MAX_CREDITS
        ));
    }
}