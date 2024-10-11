package pl.iseebugs.doread.infrastructure.module;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.modulesessioncoordinator.ModuleSessionCoordinator;
import pl.iseebugs.doread.domain.module.ModuleFacade;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.module.dto.ModuleWriteModel;
import pl.iseebugs.doread.domain.security.SecurityFacade;
import pl.iseebugs.doread.domain.session.SessionNotFoundException;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.util.List;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/module")
class ModuleController {

    ModuleFacade moduleFacade;
    SecurityFacade securityFacade;
    AppUserFacade appUserFacade;
    ModuleSessionCoordinator moduleSessionCoordinator;

    @DeleteMapping()
    public ResponseEntity<ApiResponse<Void>> deleteModule(@RequestHeader("Authorization") String authHeader, @RequestParam Long moduleId) throws EmailNotFoundException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        String userEmail = securityFacade.extractEmail(accessToken);
        AppUserReadModel user =  appUserFacade.findByEmail(userEmail);
        moduleSessionCoordinator.deleteModule(user.id(), moduleId);
        log.info("Deleted module: userId: {}, moduleId: {}", user.id(), moduleId);
        return ResponseEntity.ok(ApiResponseFactory.createResponseWithoutData(201, "Moduł usunięty pomyślnie."));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ModuleReadModel>>> getAllUserModules(@RequestHeader("Authorization") String authHeader) throws EmailNotFoundException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        String userEmail = securityFacade.extractEmail(accessToken);
        AppUserReadModel user =  appUserFacade.findByEmail(userEmail);
        List<ModuleReadModel> data = moduleFacade.findAllByUserId(user.id());
        return ResponseEntity.ok(ApiResponseFactory.createSuccessResponse("Modules List.", data));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<List<ModuleReadModel>>> createNewModule(@RequestHeader("Authorization") String authHeader) throws EmailNotFoundException, AppUserNotFoundException, ModuleNotFoundException, SessionNotFoundException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        String userEmail = securityFacade.extractEmail(accessToken);
        AppUserReadModel user =  appUserFacade.findByEmail(userEmail);
        List<ModuleReadModel> data = moduleSessionCoordinator.createNewModule(user.id());
        return ResponseEntity.ok(ApiResponseFactory.createSuccessResponse("Modules List.", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModuleReadModel>> getUserModule(@RequestHeader("Authorization") String authHeader, @PathVariable("id") @Valid Long moduleId) throws EmailNotFoundException, ModuleNotFoundException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        String userEmail = securityFacade.extractEmail(accessToken);
        AppUserReadModel user =  appUserFacade.findByEmail(userEmail);
        ModuleReadModel data = moduleFacade.findByIdAndUserId(user.id(), moduleId);
        return ResponseEntity.ok(ApiResponseFactory.createSuccessResponse("Module " + data.getModuleName(), data));
    }

    @PutMapping()
    public ResponseEntity<ApiResponse<ModuleReadModel>> putModule(@RequestHeader("Authorization") String authHeader, @RequestBody ModuleWriteModel toWrite) throws EmailNotFoundException, ModuleNotFoundException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authHeader.substring(7);
        String userEmail = securityFacade.extractEmail(accessToken);
        AppUserReadModel user =  appUserFacade.findByEmail(userEmail);
        ModuleReadModel data = moduleFacade.updateModule(user.id(), toWrite);
        return ResponseEntity.ok(ApiResponseFactory.createSuccessResponse("Module " + data.getModuleName(), data));
    }

}


