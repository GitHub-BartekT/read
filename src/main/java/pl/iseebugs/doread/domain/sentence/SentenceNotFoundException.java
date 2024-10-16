package pl.iseebugs.doread.domain.sentence;

public class SentenceNotFoundException extends Exception{
    public SentenceNotFoundException() {
        super("Sentence not found.");
    }
    public SentenceNotFoundException(String message) {
        super(message);
    }
}
