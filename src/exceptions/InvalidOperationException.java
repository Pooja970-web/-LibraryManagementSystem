package exceptions;

/** Thrown for rule violations, e.g. exceeding max borrow limit or duplicate IDs. */
public class InvalidOperationException extends Exception {
    public InvalidOperationException(String message) {
        super(message);
    }
}
