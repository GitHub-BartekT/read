package pl.iseebugs.doread.infrastructure.account;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.AccountHelper;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.delete.AccountDeleteFacade;
import pl.iseebugs.doread.domain.security.projection.LoginTokenDto;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth/delete")
class AccountDeleteController {

    AccountDeleteFacade accountDeleteFacade;
    AccountHelper accountHelper;

    @DeleteMapping()
    ResponseEntity<ApiResponse<LoginTokenDto>> deleteUser(@RequestHeader("Authorization") String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        AppUserReadModel user = accountHelper.getAppUserReadModelFromToken(accessToken);

        LoginTokenDto deleteTokenDto = accountDeleteFacade.deleteUser(user);

        ApiResponse<LoginTokenDto> response = ApiResponseFactory
                .createResponseWithStatus(
                        HttpStatus.CREATED.value(),
                        "Delete confirmation mail created successfully.",
                        deleteTokenDto);

        return ResponseEntity.ok(response);
    }
}
