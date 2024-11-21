package pl.iseebugs.doread.infrastructure.account;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.TokenNotFoundException;
import pl.iseebugs.doread.domain.account.create.AccountCreateFacade;
import pl.iseebugs.doread.domain.account.create.RegistrationTokenConflictException;
import pl.iseebugs.doread.domain.account.lifecycle.dto.LoginRequest;
import pl.iseebugs.doread.domain.account.lifecycle.dto.LoginResponse;
import pl.iseebugs.doread.domain.email.EmailSender;
import pl.iseebugs.doread.domain.email.InvalidEmailTypeException;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.security.projection.LoginTokenDto;
import pl.iseebugs.doread.domain.sentence.SentenceNotFoundException;
import pl.iseebugs.doread.domain.session.SessionNotFoundException;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth/create")
class AccountCreateController {

    AccountCreateFacade accountCreateFacade;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<LoginResponse>> signUp(@RequestBody LoginRequest signUpRequest) throws EmailSender.EmailConflictException, InvalidEmailTypeException, AppUserNotFoundException, TokenNotFoundException, ModuleNotFoundException, SessionNotFoundException, SentenceNotFoundException, EmailNotFoundException {
        return ResponseEntity.ok(accountCreateFacade.signUp(signUpRequest));
    }

    @GetMapping(path = "/refresh-confirmation-token")
    public ResponseEntity<ApiResponse<LoginTokenDto>> refreshConfirmationToken(@RequestParam("email") String email) throws TokenNotFoundException, InvalidEmailTypeException, RegistrationTokenConflictException, AppUserNotFoundException, EmailNotFoundException {
        return ResponseEntity.ok(accountCreateFacade.refreshConfirmationToken(email));
    }
}


