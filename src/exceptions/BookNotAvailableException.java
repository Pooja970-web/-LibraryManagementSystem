package exceptions;

/** Thrown when a member tries to borrow a book with zero available copies. */
public class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
