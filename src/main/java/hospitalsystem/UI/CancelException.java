package hospitalsystem.UI;

public class CancelException extends RuntimeException {
    public static final String userCancel = "User prompted for exit";

    public CancelException(String message) {
        super(message);
    }
}
