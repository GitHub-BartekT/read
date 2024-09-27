package pl.iseebugs.doread.infrastructure.account;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.lifecycle.LifecycleAccountFacade;
import pl.iseebugs.doread.domain.account.lifecycle.dto.AppUserDto;
import pl.iseebugs.doread.domain.account.lifecycle.dto.LoginRequest;
import pl.iseebugs.doread.domain.account.lifecycle.dto.LoginResponse;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
class AccountLifecycleController {

    LifecycleAccountFacade lifecycleAccountFacade;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest signInRequest) throws TokenNotFoundException, AppUserNotFoundException, EmailNotFoundException {
        return  ResponseEntity.ok(lifecycleAccountFacade.login(signInRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestHeader("Authorization") String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String refreshToken = authHeader.substring(7);
        return ResponseEntity.ok(lifecycleAccountFacade.refreshToken(refreshToken));
    }

    @PatchMapping("/users")
    ResponseEntity<ApiResponse<AppUserDto>> updateUser(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody AppUserWriteModel appUserWriteModel) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        return ResponseEntity.ok(lifecycleAccountFacade.updateUserData(accessToken, appUserWriteModel));
    }

    @PatchMapping("/users/forgotten-password")
    ResponseEntity<ApiResponse<Void>> generateNewPassword(@RequestHeader("Authorization") String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        lifecycleAccountFacade.resetPasswordAndNotify(accessToken);
        return ResponseEntity.ok(ApiResponseFactory
                .createResponseWithoutData(HttpStatus.OK.value(), "Password reset successfully and notification sent"));

    }

    @PatchMapping("/users/password")
    ResponseEntity<ApiResponse<Void>> generateNewPassword(@RequestHeader("Authorization") String authHeader, @RequestBody String newPassword) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        lifecycleAccountFacade.updatePassword(accessToken, newPassword);

        return ResponseEntity.ok(ApiResponseFactory
                .createResponseWithoutData(HttpStatus.OK.value(), "Password updated successfully"));
    }
}
