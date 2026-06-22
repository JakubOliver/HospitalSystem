package cz.cuni.kubinja.hospitalsystem.menu;

/**
 * Custom exception which is thrown when the user does not comply with the process of inputting and forcefully exit.
 */
public class CancelException extends RuntimeException {
    /** User did not comply with the process of inputting data and wanted to forcefully exit */
    public static final String userCancel = "User prompted for exit";

    /**
     * Creates CancelException with the provided message.
     *
     * @param message Message of the exception.
     */
    public CancelException(String message) {
        super(message);
    }
}
