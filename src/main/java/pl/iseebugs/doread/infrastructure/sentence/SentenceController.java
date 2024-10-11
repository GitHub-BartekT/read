package pl.iseebugs.doread.infrastructure.sentence;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.security.SecurityFacade;
import pl.iseebugs.doread.domain.sentence.SentenceFacade;
import pl.iseebugs.doread.domain.session.dto.SessionWriteModel;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/sentence")
class SentenceController {

    SentenceFacade sentenceFacade;
    SecurityFacade securityFacade;
    AppUserFacade appUserFacade;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<String>>> getAllSentenceByModuleId(@RequestHeader("Authorization") String authHeader, @PathVariable("id") Long moduleId) throws EmailNotFoundException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        String userEmail = securityFacade.extractEmail(accessToken);
        AppUserReadModel user = appUserFacade.findByEmail(userEmail);
        List<String> data = sentenceFacade.findAllSentenceByModuleId(user.id(), moduleId);
        return ResponseEntity.ok(ApiResponseFactory.createSuccessResponse("Lista sesji.", data));
    }
}
