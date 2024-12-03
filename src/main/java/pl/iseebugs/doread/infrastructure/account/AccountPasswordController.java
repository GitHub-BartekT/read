package pl.iseebugs.doread.infrastructure.account;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.create.AccountCreateFacade;
import pl.iseebugs.doread.domain.account.create.RegistrationTokenConflictException;
import pl.iseebugs.doread.domain.account.lifecycle.dto.LoginRequest;
import pl.iseebugs.doread.domain.account.lifecycle.dto.LoginResponse;
import pl.iseebugs.doread.domain.account.password.PasswordFacade;
import pl.iseebugs.doread.domain.email.EmailSender;
import pl.iseebugs.doread.domain.email.InvalidEmailTypeException;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.security.projection.LoginTokenDto;
import pl.iseebugs.doread.domain.sentence.SentenceNotFoundException;
import pl.iseebugs.doread.domain.session.SessionNotFoundException;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
class AccountPasswordController {

    PasswordFacade passwordFacade;

    @PostMapping("/password/{email}")
    public ResponseEntity<ApiResponse<Void>> getPasswordToken(@PathVariable String email) throws InvalidEmailTypeException, TokenNotFoundException, EmailNotFoundException, RegistrationTokenConflictException {
        passwordFacade.generateAndSendPasswordToken(email);
        return ResponseEntity.ok(ApiResponseFactory.createResponseWithoutData(HttpStatus.NO_CONTENT.value(), ""));
    }

    @PatchMapping("/users/password")
    ResponseEntity<ApiResponse<Void>> generateNewPassword(@RequestHeader("Authorization") String authHeader, @RequestBody String newPassword) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        passwordFacade.updatePassword(accessToken, newPassword);

        return ResponseEntity.ok(ApiResponseFactory
                .createResponseWithoutData(HttpStatus.OK.value(), "Password updated successfully"));
    }
}


