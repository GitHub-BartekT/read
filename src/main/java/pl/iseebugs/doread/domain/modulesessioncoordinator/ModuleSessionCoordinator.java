package pl.iseebugs.doread.domain.modulesessioncoordinator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.domain.module.ModuleFacade;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.module.dto.ModuleWriteModel;
import pl.iseebugs.doread.domain.sentence.SentenceFacade;
import pl.iseebugs.doread.domain.session.SessionFacade;
import pl.iseebugs.doread.domain.session.SessionNotFoundException;
import pl.iseebugs.doread.domain.session.dto.SessionWriteModel;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class ModuleSessionCoordinator {

    private final String MODULE_NAME = "Język polski";
    private final String NEW_MODULE = "Nowy moduł";

    private final ModuleFacade moduleFacade;
    private final SentenceFacade sentenceFacade;
    private final SessionFacade sessionFacade;

    public void creatingPredefinedModule(Long userId) throws AppUserNotFoundException, ModuleNotFoundException, SessionNotFoundException {
        ModuleReadModel module = moduleFacade.create(userId, MODULE_NAME);
        sentenceFacade.createSentencesFromProperties(userId, module.getId());
        SessionWriteModel session = sessionFacade.createSession(userId, MODULE_NAME);
        sessionFacade.addModuleToSession(userId,session.getId(), module.getId());
    }

    public List<ModuleReadModel> createNewModule(Long userId) throws AppUserNotFoundException, ModuleNotFoundException, SessionNotFoundException {
        ModuleReadModel module = moduleFacade.create(userId, NEW_MODULE);
        SessionWriteModel session = sessionFacade.createSession(userId, NEW_MODULE);
        sessionFacade.addModuleToSession(userId,session.getId(), module.getId());
        return moduleFacade.findAllByUserId(userId);
    }

    public void deleteModule(Long userId, Long moduleId){
        List<SessionWriteModel> sessions = sessionFacade.getSessionsForUserAndModule(userId, moduleId);
        for (SessionWriteModel session: sessions) {
            sessionFacade.deleteSession(userId, session.getId());
        }
        moduleFacade.deleteByIdAndUserId(moduleId, userId);
    }

    @Transactional
    public ModuleReadModel updateModuleWithSessionName(Long userId, ModuleWriteModel toUpdate) throws ModuleNotFoundException {
        sessionFacade.updateSessionName(userId, toUpdate);
        return moduleFacade.updateModule(userId, toUpdate);
    }
}
