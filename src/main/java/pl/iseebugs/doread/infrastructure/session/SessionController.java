package pl.iseebugs.doread.infrastructure.session;

import io.swagger.v3.oas.annotations.headers.Header;
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
import pl.iseebugs.doread.domain.email.EmailSender;
import pl.iseebugs.doread.domain.email.InvalidEmailTypeException;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.security.SecurityFacade;
import pl.iseebugs.doread.domain.security.projection.LoginTokenDto;
import pl.iseebugs.doread.domain.session.SessionFacade;
import pl.iseebugs.doread.domain.session.SessionNotFoundException;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/session")
class SessionController {

    SessionFacade sessionFacade;
    SecurityFacade securityFacade;
    AppUserFacade appUserFacade;

    @GetMapping("/next-session")
    public ResponseEntity<ApiResponse<List<String>>> nextSession(@RequestHeader("Authorization") String authHeader, @RequestParam Long sessionId) throws EmailSender.EmailConflictException, InvalidEmailTypeException, AppUserNotFoundException, TokenNotFoundException, ModuleNotFoundException, SessionNotFoundException, EmailNotFoundException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        String userEmail = securityFacade.extractEmail(accessToken);
        AppUserReadModel user =  appUserFacade.findByEmail(userEmail);
        List<String> data = sessionFacade.getNextSession(user.id(), sessionId);
        return ResponseEntity.ok(ApiResponseFactory.createSuccessResponse("Lista słów.", data));
    }

    @PatchMapping("/end-session")
    public ResponseEntity<ApiResponse<List<String>>> endSession(@RequestHeader("Authorization") String authHeader, @RequestParam Long sessionId) throws EmailSender.EmailConflictException, InvalidEmailTypeException, AppUserNotFoundException, TokenNotFoundException, ModuleNotFoundException, SessionNotFoundException, EmailNotFoundException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        String userEmail = securityFacade.extractEmail(accessToken);
        AppUserReadModel user =  appUserFacade.findByEmail(userEmail);
        sessionFacade.endSession(user.id(), sessionId);
        return ResponseEntity.ok(ApiResponseFactory.createResponseWithoutData(200, "End session."));
    }
}


