package pl.iseebugs.doread.infrastructure.account;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.create.AccountCreateFacade;
import pl.iseebugs.doread.domain.account.create.RegistrationTokenConflictException;
import pl.iseebugs.doread.domain.account.delete.AccountDeleteFacade;
import pl.iseebugs.doread.domain.account.password.PasswordFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;

@AllArgsConstructor
@Controller
class AccountConfirmMVCController {

    AccountCreateFacade accountCreateFacade;
    PasswordFacade passwordFacade;
    AccountDeleteFacade accountDeleteFacade;

    @GetMapping("/api/auth/confirm")
    public String confirm(@RequestParam("token") String token) throws RegistrationTokenConflictException, AppUserNotFoundException, TokenNotFoundException {
        try {
            accountCreateFacade.confirmToken(token);
        } catch (Exception e) {
            return "error";
        }
        return "registrationSuccess";
    }

    @GetMapping("/api/auth/password")
    public String changePassword(@RequestParam("token") String token, Model model) {
        if (passwordFacade.isTokenValid(token)) {
            model.addAttribute("token", token);
            return "reset-password-form";
        } else {
            return "error";
        }
    }

    @PostMapping("/api/auth/password")
    public String handlePasswordReset(@RequestParam("token") String token,
                                      @RequestParam("newPassword") String newPassword,
                                      Model model) throws EmailNotFoundException, AppUserNotFoundException {
        if (passwordFacade.createNewPassword(token, newPassword)) {
            model.addAttribute("header", "Od teraz masz nowe hasło!");
            model.addAttribute("text","Możesz się nim tutaj zalogować:");
            return "success";
        }
        return "error";
    }

    @GetMapping("api/auth/delete/delete-confirm")
    public String deleteConfirm(@RequestParam("token") String token) throws TokenNotFoundException, AppUserNotFoundException, EmailNotFoundException {
        try {
            accountDeleteFacade.confirmDeleteToken(token);
        } catch (Exception e) {
            return "error";
        }
        return "accountDeletionSuccess";

    }
}