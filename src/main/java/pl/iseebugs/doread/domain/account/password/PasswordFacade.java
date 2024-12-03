package pl.iseebugs.doread.domain.account.password;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.account.AccountHelper;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.create.RegistrationTokenConflictException;
import pl.iseebugs.doread.domain.account.lifecycle.dto.AppUserDto;
import pl.iseebugs.doread.domain.email.EmailFacade;
import pl.iseebugs.doread.domain.email.EmailType;
import pl.iseebugs.doread.domain.email.InvalidEmailTypeException;
import pl.iseebugs.doread.domain.security.SecurityFacade;
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

    private final PasswordTokenService passwordTokenService;
    private final AccountHelper accountHelper;
    private final AppUserFacade appUserFacade;
    private final SecurityFacade securityFacade;
    private final EmailFacade emailFacade;

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
            String encodePassword = securityFacade.passwordEncode(newPassword);
            AppUserReadModel appUserFromDB = appUserFacade.findUserById(existingPasswordToken.orElseThrow().getAppUserId());
            AppUserWriteModel toUpdate = AppUserWriteModel.builder()
                    .id(appUserFromDB.id())
                    .password(encodePassword)
                    .build();
            AppUserReadModel updated = appUserFacade.update(toUpdate);
            return true;
        }
        return false;
    }

    public void updatePassword(String accessToken, String newPassword) throws InvalidEmailTypeException, AppUserNotFoundException, EmailNotFoundException {
        AppUserReadModel appUserFromDB = accountHelper.getAppUserReadModelFromToken(accessToken);
        updatePasswordAndNotify(newPassword, appUserFromDB);
    }

    private void updatePasswordAndNotify(final String newPassword, final AppUserReadModel appUserFromDB) throws AppUserNotFoundException, EmailNotFoundException, InvalidEmailTypeException {
        String encodePassword = securityFacade.passwordEncode(newPassword);

        AppUserWriteModel toUpdate = AppUserWriteModel.builder()
                .id(appUserFromDB.id())
                .password(encodePassword)
                .build();

        AppUserReadModel updated = appUserFacade.update(toUpdate);
        AppUserDto responseDTO = AccountHelper.mapUserToDto(updated);

        emailFacade.sendTemplateEmail(
                EmailType.RESET,
                responseDTO,
                newPassword);
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
