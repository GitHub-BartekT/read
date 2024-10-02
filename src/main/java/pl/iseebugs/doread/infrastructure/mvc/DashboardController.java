package pl.iseebugs.doread.infrastructure.mvc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.security.SecurityFacade;
import pl.iseebugs.doread.domain.session.SessionFacade;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

@Controller
@AllArgsConstructor
@RequestMapping("/api")
class DashboardController {

    SecurityFacade securityFacade;
    AppUserFacade appUserFacade;
    SessionFacade sessionFacade;

    @GetMapping("/dashboard")
    public String getDashboard(Model model, @RequestHeader("Authorization") String authHeader) throws EmailNotFoundException {

     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
         model.addAttribute("message", "Próbujesz dostać się do zasobów, do których nie masz dostępu.");
         return "unauthorized";
        }
        String accessToken = authHeader.substring(7);
        String userEmail = securityFacade.extractEmail(accessToken);
        AppUserReadModel user = appUserFacade.findByEmail(userEmail);

        var sessions = sessionFacade.findAllSessionsByUserId(user.id());
        model.addAttribute("userSessions", sessions);
        return "dashboard";
    }
}

