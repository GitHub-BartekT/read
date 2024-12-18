package pl.iseebugs.doread.domain.account;

import org.springframework.stereotype.Component;
import pl.iseebugs.doread.domain.account.lifecycle.dto.AppUserDto;
import pl.iseebugs.doread.domain.email.EmailFacade;
import pl.iseebugs.doread.domain.email.EmailType;
import pl.iseebugs.doread.domain.email.InvalidEmailTypeException;
import pl.iseebugs.doread.domain.security.AuthorizationProperties;
import pl.iseebugs.doread.domain.security.SecurityFacade;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.util.UUID;

@Component
public class AccountHelper {

    public static long CONFIRMATION_ACCOUNT_TOKEN_EXPIRATION_TIME;
    public static long DELETE_ACCOUNT_TOKEN_EXPIRATION_TIME;

    private final AppProperties appProperties;
    private final EmailFacade emailFacade;
    private final SecurityFacade securityFacade;
    private final AppUserFacade appUserFacade;

    AccountHelper(AppProperties appProperties,
    EmailFacade emailFacade,
    SecurityFacade securityFacade,
    AppUserFacade appUserFacade,
    AuthorizationProperties authorizationProperties ){
        this.appProperties = appProperties;
        this.emailFacade = emailFacade;
        this.securityFacade = securityFacade;
        this.appUserFacade = appUserFacade;
        CONFIRMATION_ACCOUNT_TOKEN_EXPIRATION_TIME = authorizationProperties.getExpirationConfirmationTokenTime();
        DELETE_ACCOUNT_TOKEN_EXPIRATION_TIME = authorizationProperties.getExpirationDeleteTokenTime();
    }

    public void sendMailWithConfirmationToken(final String email, final String endpoint, final String token) throws InvalidEmailTypeException {
        sendMailWithToken(EmailType.ACTIVATION, email, endpoint, token);
    }

    public void sendMailWithPasswordToken(final String email, final String endpoint, final String token) throws InvalidEmailTypeException {
        sendMailWithToken(EmailType.RESET, email, endpoint, token);
    }

    public void sendMailWithDeleteToken(final String email, final String endpoint, final String token) throws InvalidEmailTypeException {
        sendMailWithToken(EmailType.DELETE, email, endpoint, token);
    }

    private void sendMailWithToken(EmailType emailType, final String email, final String endpoint, final String token) throws InvalidEmailTypeException {
        AppUserDto dataToEmail = AppUserDto.builder()
                .firstName(null)
                .email(email).build();

        String link = createUrl(endpoint, token);
        emailFacade.sendTemplateEmail(emailType, dataToEmail, link);
    }

    public String createUrl(final String endpoint, final String token) {
        return appProperties.uri() + ":" +
                appProperties.port() +
                endpoint +
                token;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public AppUserReadModel getAppUserReadModelFromToken(final String accessToken) throws EmailNotFoundException {
        String userEmail = securityFacade.extractEmail(accessToken);
        return appUserFacade.findByEmail(userEmail);
    }

    public static AppUserDto mapUserToDto(AppUserReadModel user) {
        return AppUserDto.builder()
                .id(user.id())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .email(user.email())
                .build();
    }
}
