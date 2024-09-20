package pl.iseebugs.doread.domain.account.create;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;
import pl.iseebugs.doread.domain.email.EmailSender;
import pl.iseebugs.doread.domain.email.InvalidEmailTypeException;
import pl.iseebugs.doread.domain.user.AppUserFacade;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Component
class CreateAccountValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    static AppUserFacade appUserFacade;


    CreateAccountValidator(AppUserFacade appUserFacade){
        this.appUserFacade = appUserFacade;
    }

    void validateConfirmationToken(final ConfirmationToken confirmationToken) throws RegistrationTokenConflictException {
        if (confirmationToken.getConfirmedAt() != null) {
            log.info("Confirmation token already confirmed.");
            throw new RegistrationTokenConflictException();
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        validateTokenExpiration(expiredAt);
    }

    void validateEmailConflict(final String email) throws EmailSender.EmailConflictException {
        if (appUserFacade.existsByEmail(email)) {
            throw new EmailSender.EmailConflictException();
        }
    }

    private static void validateTokenExpiration(final LocalDateTime expiredAt) {
        if (expiredAt.isBefore(LocalDateTime.now())) {
            log.info("Token expired.");
            throw new CredentialsExpiredException("Token expired.");
        }
    }

    public void isValidEmail(String email) throws InvalidEmailTypeException {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new InvalidEmailTypeException("Niepoprawny format adresu e-mail");
        }
    }
}
