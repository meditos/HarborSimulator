package harborsimulator;

public class BadDataFileFormatException extends Exception {

    /**
     * Creates a new instance of
     * <code>BadDataFileFormatException</code> without detail message.
     */
    public BadDataFileFormatException() {
    }

    /**
     * Constructs an instance of
     * <code>BadDataFileFormatException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public BadDataFileFormatException(String msg) {
        super(msg);
    }
}
