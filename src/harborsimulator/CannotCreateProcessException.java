package harborsimulator;

public class CannotCreateProcessException extends Exception {

    /**
     * Creates a new instance of
     * <code>CannotCreateProcessException</code> without detail message.
     */
    public CannotCreateProcessException() {
    }

    /**
     * Constructs an instance of
     * <code>CannotCreateProcessException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public CannotCreateProcessException(String msg) {
        super(msg);
    }
}
