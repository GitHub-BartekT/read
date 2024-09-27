package pl.iseebugs.doread.infrastructure.account;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.create.AccountCreateFacade;
import pl.iseebugs.doread.domain.account.create.RegistrationTokenConflictException;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;

@AllArgsConstructor
@Controller
class AccountConfirmMVCController {

    AccountCreateFacade accountCreateFacade;

    @GetMapping("/api/auth/confirm")
    public String confirm(@RequestParam("token") String token) throws RegistrationTokenConflictException, AppUserNotFoundException, TokenNotFoundException {
        accountCreateFacade.confirmToken(token);
        return "registrationSuccess";
    }
}