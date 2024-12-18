package pl.iseebugs.doread.domain.account.create;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.AccountHelper;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.lifecycle.LifecycleAccountFacade;
import pl.iseebugs.doread.domain.account.lifecycle.dto.LoginRequest;
import pl.iseebugs.doread.domain.account.lifecycle.dto.LoginResponse;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.modulesessioncoordinator.ModuleSessionCoordinatorFacade;
import pl.iseebugs.doread.domain.email.EmailSender;
import pl.iseebugs.doread.domain.email.InvalidEmailTypeException;
import pl.iseebugs.doread.domain.security.SecurityFacade;
import pl.iseebugs.doread.domain.security.projection.LoginTokenDto;
import pl.iseebugs.doread.domain.sentence.SentenceNotFoundException;
import pl.iseebugs.doread.domain.session.SessionNotFoundException;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static pl.iseebugs.doread.domain.account.AccountHelper.CONFIRMATION_ACCOUNT_TOKEN_EXPIRATION_TIME;
import static pl.iseebugs.doread.domain.account.AccountHelper.getUUID;

@Log4j2
@Service
public class AccountCreateFacade {

    private static final String USER_ROLE = "USER";
    private static final String TOKEN_CONFIRMATION_ENDPOINT = "/api/auth/confirm?token=";
    private static final int TOKEN_CREATED_STATUS = 201;
    private static final int TOKEN_EXISTS_STATUS = 204;
    private static final int ACCOUNT_ALREADY_CONFIRMED_STATUS = 401;

    SecurityFacade securityFacade;
    AppUserFacade appUserFacade;
    ConfirmationTokenService confirmationTokenService;
    AccountHelper accountHelper;
    CreateAccountValidator createAccountValidator;
    ModuleSessionCoordinatorFacade moduleSessionCoordinatorFacade;
    private final LifecycleAccountFacade lifecycleAccountFacade;

    AccountCreateFacade(SecurityFacade securityFacade,
                        AppUserFacade appUserFacade,
                        ConfirmationTokenService confirmationTokenService,
                        AccountHelper accountHelper,
                        CreateAccountValidator createAccountValidator,
                        ModuleSessionCoordinatorFacade moduleSessionCoordinatorFacade, final LifecycleAccountFacade lifecycleAccountFacade) {
        this.securityFacade = securityFacade;
        this.appUserFacade = appUserFacade;
        this.confirmationTokenService = confirmationTokenService;
        this.accountHelper = accountHelper;
        this.createAccountValidator = createAccountValidator;
        this.moduleSessionCoordinatorFacade = moduleSessionCoordinatorFacade;
        this.lifecycleAccountFacade = lifecycleAccountFacade;
    }

    public ApiResponse<LoginResponse>  signUp(LoginRequest registrationRequest) throws EmailSender.EmailConflictException, InvalidEmailTypeException, AppUserNotFoundException, TokenNotFoundException, ModuleNotFoundException, SessionNotFoundException, SentenceNotFoundException, EmailNotFoundException {
        String email = registrationRequest.getEmail();
        String password = securityFacade.passwordEncode(registrationRequest.getPassword());

        createAccountValidator.isValidEmail(email);
        createAccountValidator.validateEmailConflict(email);

        AppUserReadModel createdUser = createAppUser(email, password);
        String token = confirmationTokenService.createNewConfirmationToken(createdUser.id());
        moduleSessionCoordinatorFacade.creatingPredefinedModule(createdUser.id());

        sendConfirmationEmail(email, token);
        return lifecycleAccountFacade.login(registrationRequest);
    }

    public ApiResponse<Void> confirmToken(final String token) throws TokenNotFoundException, RegistrationTokenConflictException, AppUserNotFoundException {
        ConfirmationToken confirmationToken = confirmationTokenService.getTokenByToken(token)
                .orElseThrow(TokenNotFoundException::new);

        createAccountValidator.validateConfirmationToken(confirmationToken);

        confirmationTokenService.setConfirmedAt(token);
        appUserFacade.confirmAccount(confirmationToken.getAppUserId());
        return ApiResponseFactory.createResponseWithoutData(HttpStatus.OK.value(), "Account successfully confirmed");
    }

    public ApiResponse<LoginTokenDto> refreshConfirmationToken(final String email) throws InvalidEmailTypeException, TokenNotFoundException, RegistrationTokenConflictException, AppUserNotFoundException, EmailNotFoundException {
        AppUserReadModel user = appUserFacade.findByEmail(email);
        Optional<ConfirmationToken> existingToken = confirmationTokenService.getTokenByUserId(user.id());

        if (existingToken.isPresent()) {
            return handleExistingConfirmationToken(existingToken.get(), user);
        } else {
            return generateNewConfirmationToken(user.id(), email);
        }
    }

    private ApiResponse<LoginTokenDto> handleExistingConfirmationToken(ConfirmationToken existingToken, AppUserReadModel user) throws RegistrationTokenConflictException, InvalidEmailTypeException, TokenNotFoundException {
        if (existingToken.getConfirmedAt() != null) {
            return ApiResponseFactory.createResponseWithoutData(ACCOUNT_ALREADY_CONFIRMED_STATUS, "Account already confirmed.");
        }

        String token = refreshConfirmationToken(existingToken);
        sendConfirmationEmail(user.email(), token);
        Date tokenExpiresAt = confirmationTokenService.calculateTokenExpiration(token);
        return ApiResponseFactory.createResponseWithStatus(TOKEN_EXISTS_STATUS, "Successfully generated new confirmation token.", new LoginTokenDto(token, tokenExpiresAt));
    }

    private String refreshConfirmationToken(ConfirmationToken existingToken) {
        String newToken = getUUID();
        existingToken.setToken(newToken);
        existingToken.setCreatedAt(LocalDateTime.now());
        existingToken.setExpiresAt(LocalDateTime.now().plusMinutes(CONFIRMATION_ACCOUNT_TOKEN_EXPIRATION_TIME));
        confirmationTokenService.saveConfirmationToken(existingToken);
        return newToken;
    }

    private ApiResponse<LoginTokenDto> generateNewConfirmationToken(Long userId, String email) throws InvalidEmailTypeException, TokenNotFoundException {
        String token = confirmationTokenService.createNewConfirmationToken(userId);
        sendConfirmationEmail(email, token);
        Date tokenExpiresAt = confirmationTokenService.calculateTokenExpiration(token);
        return ApiResponseFactory.createResponseWithStatus(TOKEN_CREATED_STATUS, "Successfully generated new confirmation token.", new LoginTokenDto(token, tokenExpiresAt));
    }

    public ConfirmationToken getTokenByUserId(Long userId) throws TokenNotFoundException {
        return confirmationTokenService.getTokenByUserId(userId)
                .orElseThrow(() -> new TokenNotFoundException("Confirmation token not found."));
    }

    private AppUserReadModel createAppUser(final String email, final String password) throws AppUserNotFoundException {
        AppUserWriteModel newUser = AppUserWriteModel.builder()
                .email(email)
                .password(password)
                .role(USER_ROLE)
                .locked(false)
                .enabled(true)
                .build();
        return appUserFacade.create(newUser);
    }

    private void sendConfirmationEmail(String email, String token) throws InvalidEmailTypeException {
        accountHelper.sendMailWithConfirmationToken(email, TOKEN_CONFIRMATION_ENDPOINT, token);
    }
}
