package pl.iseebugs.doread.domain.account.lifecycle;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.AccountHelper;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.lifecycle.dto.AppUserDto;
import pl.iseebugs.doread.domain.account.lifecycle.dto.LoginRequest;
import pl.iseebugs.doread.domain.account.lifecycle.dto.LoginResponse;
import pl.iseebugs.doread.domain.email.EmailFacade;
import pl.iseebugs.doread.domain.email.EmailType;
import pl.iseebugs.doread.domain.email.InvalidEmailTypeException;
import pl.iseebugs.doread.domain.security.SecurityFacade;
import pl.iseebugs.doread.domain.security.projection.LoginTokenDto;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

import java.util.Date;

import static pl.iseebugs.doread.domain.account.AccountHelper.getUUID;

@Log4j2
@Service
@AllArgsConstructor
public class LifecycleAccountFacade {

    private final AppUserFacade appUserFacade;
    private final SecurityFacade securityFacade;
    private final AccountHelper accountHelper;

    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) throws TokenNotFoundException, EmailNotFoundException {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        AppUserReadModel user = appUserFacade.findByEmail(email);
        securityFacade.authenticateByAuthenticationManager(email, password);

        return ApiResponseFactory.createSuccessResponse("Login success",buildLoginResponse(user));
    }

    private LoginResponse buildLoginResponse(AppUserReadModel user){
        LoginTokenDto accessToken = securityFacade.generateAccessToken(user);
        LoginTokenDto refreshToken = securityFacade.generateRefreshToken(user);

        return createLoginResponse(accessToken,refreshToken);
    }

    public ApiResponse<LoginResponse> refreshToken(String refreshToken) throws Exception {
        AppUserReadModel user = accountHelper.getAppUserReadModelFromToken(refreshToken);
        LoginTokenDto accessToken = securityFacade.generateAccessToken(user);

        Date refreshTokenExpiresAt = securityFacade.extractExpiresAt(refreshToken);
        LoginTokenDto loginTokenDto = new LoginTokenDto(refreshToken, refreshTokenExpiresAt);
        return ApiResponseFactory.createSuccessResponse("Access Token refreshed.", createLoginResponse(accessToken,loginTokenDto));
    }

    public ApiResponse<AppUserDto> updateUserData(String accessToken, AppUserWriteModel toWrite) throws Exception {
        AppUserReadModel appUserFromDataBase = accountHelper.getAppUserReadModelFromToken(accessToken);

        String firstName = toWrite.getFirstName().isBlank() ?
                appUserFromDataBase.firstName() :
                toWrite.getFirstName();
        String lastName = toWrite.getLastName().isBlank() ?
                appUserFromDataBase.lastName() :
                toWrite.getLastName();

        AppUserWriteModel toUpdate = buildUpdatedUserModel(appUserFromDataBase, firstName, lastName);

        AppUserReadModel ourUserResult = appUserFacade.updatePersonalData(toUpdate);
        return ApiResponseFactory.createSuccessResponse("Account data update successfully.",AccountHelper.mapUserToDto(ourUserResult));
    }

    private LoginResponse createLoginResponse(LoginTokenDto accessToken, LoginTokenDto refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken.token())
                .accessTokenExpiresAt(accessToken.expiresAt())
                .refreshToken(refreshToken.token())
                .refreshTokenExpiresAt(refreshToken.expiresAt())
                .build();
    }

    private AppUserWriteModel buildUpdatedUserModel(AppUserReadModel existingUser, String firstName, String lastName) {
        return AppUserWriteModel.builder()
                .id(existingUser.id())
                .email(existingUser.email())
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
