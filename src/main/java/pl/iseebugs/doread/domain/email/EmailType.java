package pl.iseebugs.doread.domain.email;

public enum EmailType {
    ACTIVATION("activation"),
    EXPIRED("token-expired"),
    SUCCESS("activation-successfully"),
    RESET("reset-password"),
    DELETE("delete-account");

    private final String template;

    private EmailType(String template) {
        this.template = template;
    }

    @Override
    public String toString() {
        return template;
    }
}
