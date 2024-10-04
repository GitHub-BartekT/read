package pl.iseebugs.doread.infrastructure.mvc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.security.SecurityFacade;
import pl.iseebugs.doread.domain.session.SessionFacade;
import pl.iseebugs.doread.domain.session.SessionNotFoundException;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/view")
class SessionMVCController {

    SecurityFacade securityFacade;
    AppUserFacade appUserFacade;
    SessionFacade sessionFacade;

    @GetMapping("/session")
    public String getSession(@RequestParam Long sessionId, Model model, @RequestHeader("Authorization") String authHeader) throws EmailNotFoundException, ModuleNotFoundException, AppUserNotFoundException, SessionNotFoundException {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            model.addAttribute("message", "Próbujesz dostać się do zasobów, do których nie masz dostępu.");
            return "unauthorized";
        }

        String accessToken = authHeader.substring(7);
        String userEmail = securityFacade.extractEmail(accessToken);
        AppUserReadModel user = appUserFacade.findByEmail(userEmail);

        List<String> sentences = sessionFacade.getNextSession(user.id(), sessionId);
        model.addAttribute("sessionSentences", sentences);
        return "session";
    }
}

