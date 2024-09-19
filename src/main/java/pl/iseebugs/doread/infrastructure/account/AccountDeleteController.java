package pl.iseebugs.doread.infrastructure.account;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.delete.AccountDeleteFacade;
import pl.iseebugs.doread.domain.security.projection.LoginTokenDto;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth/delete")
class AccountDeleteController {

    AccountDeleteFacade accountDeleteFacade;

    @DeleteMapping()
    ResponseEntity<ApiResponse<LoginTokenDto>> deleteUser(@RequestHeader("Authorization") String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        return ResponseEntity.ok(accountDeleteFacade.deleteUser(accessToken));
    }

    @GetMapping("/delete-confirm")
    public ResponseEntity<ApiResponse<Void>> deleteConfirm(@RequestParam("token") String token) throws TokenNotFoundException, AppUserNotFoundException, EmailNotFoundException {
        return ResponseEntity.ok(accountDeleteFacade.confirmDeleteToken(token));
    }
}
