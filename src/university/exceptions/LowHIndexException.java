package university.exceptions;

public class LowHIndexException extends Exception {

    private final int actualHIndex;

    public LowHIndexException(String supervisorName, int actualHIndex) {
        super(String.format(
                "Cannot assign '%s' as supervisor: h-index is %d, minimum required is 3.",
                supervisorName, actualHIndex
        ));
        this.actualHIndex = actualHIndex;
    }

    public int getActualHIndex() {
        return actualHIndex;
    }
}