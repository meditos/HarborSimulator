package harborsimulator;

class CannotSetupOntologyException extends Exception {
    
    /**
     * Creates a new instance of
     * <code>CannotSetupOntologyException</code> without detail message.
     */
    public CannotSetupOntologyException() {
    }

    /**
     * Constructs an instance of
     * <code>CannotSetupOntologyException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public CannotSetupOntologyException(String msg) {
        super(msg);
    }
    
}
