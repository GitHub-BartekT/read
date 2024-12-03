package pl.iseebugs.doread.domain.account.password;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.account.AccountHelper;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.create.RegistrationTokenConflictException;
import pl.iseebugs.doread.domain.email.InvalidEmailTypeException;
import pl.iseebugs.doread.domain.security.SecurityFacade;
import pl.iseebugs.doread.domain.user.AppUser;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class PasswordFacade {

    private static final String SET_PASSWORD_ENDPOINT = "/api/auth/password?token=";
    private static final int TOKEN_STATUS = 200;

    private final PasswordTokenService passwordTokenService;
    private final AccountHelper accountHelper;
    private final AppUserFacade appUserFacade;
    private final SecurityFacade securityFacade;

    public void generateAndSendPasswordToken(String email) throws EmailNotFoundException, InvalidEmailTypeException, TokenNotFoundException, RegistrationTokenConflictException {
        AppUserReadModel user = appUserFacade.findByEmail(email);
        Optional<PasswordToken> existingPasswordToken = passwordTokenService.getTokenByUserId(user.id());

        String token;
        if (existingPasswordToken.isPresent()) {
            existingPasswordToken.get().setConfirmedAt(null);
            token = refreshExistingPasswordToken(existingPasswordToken.get());
        } else {
            token = generateNewPasswordToken(email);
        }
        sendPasswordTokenEmail(email, token);
    }

    public boolean createNewPassword(String token, String newPassword) throws AppUserNotFoundException, EmailNotFoundException {
        Optional<PasswordToken> existingPasswordToken = passwordTokenService.getTokenByToken(token);
        if (isTokenValid(token)) {
            log.info("new password: {}", newPassword);
            String encodePassword = securityFacade.passwordEncode(newPassword);
            AppUserReadModel appUserFromDB = appUserFacade.findUserById(existingPasswordToken.orElseThrow().getAppUserId());
            AppUserWriteModel toUpdate = AppUserWriteModel.builder()
                    .id(appUserFromDB.id())
                    .password(encodePassword)
                    .build();
            AppUserReadModel updated = appUserFacade.update(toUpdate);
            log.info("created password: {}", updated.password());
            return true;
        }
        return false;
    }

    private String refreshExistingPasswordToken(PasswordToken existingToken) {
        return passwordTokenService.refreshPasswordToken(existingToken);
    }

    private String generateNewPasswordToken(String email) throws EmailNotFoundException {
        return passwordTokenService.createPasswordToken(email);
    }

    private void sendPasswordTokenEmail(String email, String token) throws InvalidEmailTypeException {
        accountHelper.sendMailWithPasswordToken(email, SET_PASSWORD_ENDPOINT, token);
    }

    public boolean isTokenValid(final String token) {
        Optional<PasswordToken> passwordToken = passwordTokenService.getTokenByToken(token);
        return passwordToken.isPresent() && passwordToken.get().getConfirmedAt() == null && passwordToken.get().getExpiresAt().isAfter(LocalDateTime.now());
    }

}
