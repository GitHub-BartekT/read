package pl.iseebugs.doread.domain.account.password;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.AccountHelper;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.create.ConfirmationToken;
import pl.iseebugs.doread.domain.account.create.RegistrationTokenConflictException;
import pl.iseebugs.doread.domain.email.EmailFacade;
import pl.iseebugs.doread.domain.email.InvalidEmailTypeException;
import pl.iseebugs.doread.domain.security.projection.LoginTokenDto;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.util.Date;
import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class PasswordFacade {

    private static final String SET_PASSWORD_ENDPOINT = "/api/auth/confirm?token=";
    private static final int TOKEN_STATUS = 200;

    private final PasswordTokenService passwordTokenService;
    private final EmailFacade emailFacade;
    private final AccountHelper accountHelper;
    private final AppUserFacade appUserFacade;

    public void generateAndSendPasswordToken(String email) throws EmailNotFoundException, InvalidEmailTypeException, TokenNotFoundException, RegistrationTokenConflictException {
        AppUserReadModel user = appUserFacade.findByEmail(email);

        Optional<PasswordToken> existingPasswordToken = passwordTokenService.getTokenByToken(email);

        String token = null;
        if(existingPasswordToken.isPresent()){
            token = refreshExistingPasswordToken(existingPasswordToken.get());
        } else {
            token = generateNewPasswordToken(email);
        }
        sendPasswordTokenEmail(email, token);
    }

    private String refreshExistingPasswordToken(PasswordToken existingToken){
        return passwordTokenService.refreshPasswordToken(existingToken);
    }

    private String generateNewPasswordToken(String email) throws EmailNotFoundException {
        return passwordTokenService.createPasswordToken(email);
    }

    private void sendPasswordTokenEmail(String email, String token) throws InvalidEmailTypeException {
        accountHelper.sendMailWithPasswordToken(email, SET_PASSWORD_ENDPOINT, token);
    }
}
