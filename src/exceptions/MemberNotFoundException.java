package exceptions;

/** Thrown when an operation references a member ID that doesn't exist. */
public class MemberNotFoundException extends Exception {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
