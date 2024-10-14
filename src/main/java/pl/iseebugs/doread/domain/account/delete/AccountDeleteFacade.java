package pl.iseebugs.doread.domain.account.delete;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.AccountHelper;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.security.projection.LoginTokenDto;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static pl.iseebugs.doread.domain.account.AccountHelper.DELETE_ACCOUNT_TOKEN_EXPIRATION_TIME;
import static pl.iseebugs.doread.domain.account.AccountHelper.getUUID;

@Log4j2
@Service
@AllArgsConstructor
public class AccountDeleteFacade {

    private static final String DELETE_CONFIRMATION_ENDPOINT = "/api/auth/delete/delete-confirm?token=";

    private final DeleteTokenService deleteTokenService;
    private final AppUserFacade appUserFacade;
    private final AccountHelper accountHelper;

    public LoginTokenDto deleteUser(AppUserReadModel user) throws Exception {
        String token = generateDeleteToken(user);

        accountHelper.sendMailWithDeleteToken(user.email(), DELETE_CONFIRMATION_ENDPOINT, token);

        Date tokenExpiresAt = deleteTokenService.calculateTokenExpiration(token);
        LoginTokenDto deleteTokenDto = new LoginTokenDto(token, tokenExpiresAt);
        return deleteTokenDto;
    }

    private String generateDeleteToken(final AppUserReadModel user){
        DeleteToken deleteToken = deleteTokenService.getTokenByUserId(user.id()).orElseGet(DeleteToken::new);

        deleteToken.setToken(getUUID());
        deleteToken.setCreatedAt(LocalDateTime.now());
        deleteToken.setExpiresAt(LocalDateTime.now().plusMinutes(DELETE_ACCOUNT_TOKEN_EXPIRATION_TIME));
        deleteToken.setAppUserId(user.id());

        deleteTokenService.saveDeleteToken(deleteToken);

        return deleteToken.getToken();
    }

    public void confirmDeleteToken(final String token) throws TokenNotFoundException, AppUserNotFoundException, EmailNotFoundException {
        DeleteToken deleteToken = deleteTokenService.getToken(token)
                .orElseThrow(TokenNotFoundException::new);

        LocalDateTime expiredAt = deleteToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new CredentialsExpiredException("Token expired.");
        }

        deleteTokenService.setConfirmedAt(token);

        appUserFacade.anonymization(deleteToken.getAppUserId());
    }
}
