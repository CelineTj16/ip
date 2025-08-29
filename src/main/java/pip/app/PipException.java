package pip.app;

/** Application-level checked exception for user-visible errors and recoverable failures */
public class PipException extends Exception {
    /**
     * Creates a new PipException with a human-readable message.
     *
     * @param message error description shown to the user
     */
    public PipException(String message) {
        super(message);
    }
}
