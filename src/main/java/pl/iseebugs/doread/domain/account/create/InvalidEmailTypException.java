package pl.iseebugs.doread.domain.account.create;

public class InvalidEmailTypException extends Exception{
    public InvalidEmailTypException() {
        super("Invalid email type exception.");
    }
    public InvalidEmailTypException(String message) {
        super(message);
    }
}
