package pl.iseebugs.doread.domain.account.password;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.create.ConfirmationToken;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static pl.iseebugs.doread.domain.account.AccountHelper.CONFIRMATION_ACCOUNT_TOKEN_EXPIRATION_TIME;
import static pl.iseebugs.doread.domain.account.AccountHelper.getUUID;

@Log4j2
@Service
@AllArgsConstructor
class PasswordTokenService {

    private final PasswordTokenRepository passwordTokenRepository;
    private final AppUserFacade appUserFacade;

    public String createPasswordToken(String email) throws EmailNotFoundException {
        AppUserReadModel user = appUserFacade.findByEmail(email);
        String token = getUUID();

        PasswordToken passwordToken = new PasswordToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(CONFIRMATION_ACCOUNT_TOKEN_EXPIRATION_TIME),
                user.id()
        );
        passwordTokenRepository.save(passwordToken);
        return token;
    }

    public String refreshPasswordToken(PasswordToken existingToken) {
        String newToken = getUUID();
        existingToken.setToken(newToken);
        existingToken.setCreatedAt(LocalDateTime.now());
        existingToken.setExpiresAt(LocalDateTime.now().plusMinutes(CONFIRMATION_ACCOUNT_TOKEN_EXPIRATION_TIME));
        passwordTokenRepository.save(existingToken);
        return newToken;
    }

    public Date calculateTokenExpiration(String token) throws TokenNotFoundException {
        PasswordToken confirmationToken = getTokenByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found."));
        return java.util.Date.from(confirmationToken.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant());
    }

    public Optional<PasswordToken> getTokenByToken(String token) {
        return passwordTokenRepository.findByToken(token);
    }
}
