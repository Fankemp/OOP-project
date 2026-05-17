package exceptions;

public class LowHIndexException extends Exception {

    private final int actualHIndex;

    public LowHIndexException(String supervisorName, int hIndex) {
        super("Cannot assign '" + supervisorName + "' as supervisor: h-index is " +
              hIndex + ", minimum required is 3.");
        this.actualHIndex = hIndex;
    }

    public int getActualHIndex() {
        return actualHIndex;
    }
}