package pl.iseebugs.doread.infrastructure.account;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.create.AccountCreateFacade;
import pl.iseebugs.doread.domain.account.create.RegistrationTokenConflictException;
import pl.iseebugs.doread.domain.account.delete.AccountDeleteFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;

@AllArgsConstructor
@Controller
class AccountConfirmMVCController {

    AccountCreateFacade accountCreateFacade;
    AccountDeleteFacade accountDeleteFacade;

    @GetMapping("/api/auth/confirm")
    public String confirm(@RequestParam("token") String token) throws RegistrationTokenConflictException, AppUserNotFoundException, TokenNotFoundException {
        try {accountCreateFacade.confirmToken(token);}
        catch (Exception e){
            return "error";
        }
        return "registrationSuccess";
    }

    @GetMapping("api/auth/delete/delete-confirm")
    public String deleteConfirm(@RequestParam("token") String token) throws TokenNotFoundException, AppUserNotFoundException, EmailNotFoundException {
        try {accountDeleteFacade.confirmDeleteToken(token);}
        catch (Exception e){
            return "error";
        }
        return "accountDeletionSuccess";

    }
}