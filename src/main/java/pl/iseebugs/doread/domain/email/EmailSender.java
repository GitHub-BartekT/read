package pl.iseebugs.doread.domain.email;

public interface EmailSender
{
    void send(String to, String subject, String email);

    class EmailConflictException extends Exception{
        public EmailConflictException() {
            super("Już istnieje konto z takim emailem.");
        }
        public EmailConflictException(String message) {
            super(message);
        }
    }
}
