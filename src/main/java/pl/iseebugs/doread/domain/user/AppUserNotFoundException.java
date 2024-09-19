package pl.iseebugs.doread.domain.user;

public class AppUserNotFoundException extends Exception {
    public AppUserNotFoundException() {
        super("User not found.");
    }

    public AppUserNotFoundException (String message) {
        super(message);
    }
}
