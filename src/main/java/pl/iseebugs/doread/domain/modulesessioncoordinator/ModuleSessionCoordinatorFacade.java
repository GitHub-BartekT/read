package pl.iseebugs.doread.domain.modulesessioncoordinator;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.module.ModuleFacade;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.module.dto.ModuleWriteModel;
import pl.iseebugs.doread.domain.sentence.SentenceFacade;
import pl.iseebugs.doread.domain.sentence.SentenceNotFoundException;
import pl.iseebugs.doread.domain.sentence.SentencesProperties;
import pl.iseebugs.doread.domain.sentence.dto.SentenceReadModel;
import pl.iseebugs.doread.domain.sentence.dto.SentenceWriteModel;
import pl.iseebugs.doread.domain.session.SessionFacade;
import pl.iseebugs.doread.domain.session.SessionNotFoundException;
import pl.iseebugs.doread.domain.session.dto.SessionWriteModel;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.infrastructure.context.RequestDataContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Log4j2
@Service
@AllArgsConstructor
public class ModuleSessionCoordinatorFacade {

    private final String MODULE_NAME = "Język polski";
    private final String NEW_MODULE = "Nowy moduł";

    private final ModuleFacade moduleFacade;
    private final SessionFacade sessionFacade;
    private final SentencesProperties sentencesProperties;
    private final SentenceFacade sentenceFacade;
    private final RequestDataContext requestDataContext;


    public void creatingPredefinedModule(Long userId) throws AppUserNotFoundException, ModuleNotFoundException, SessionNotFoundException, SentenceNotFoundException {
        ModuleReadModel module = moduleFacade.createModule(userId, MODULE_NAME);
        createSentencesFromProperties(userId, module.getId());
        SessionWriteModel session = sessionFacade.createSession(userId, MODULE_NAME);
        sessionFacade.addModuleToSession(userId,session.getId(), module.getId());
    }

    public List<ModuleReadModel> createNewModule() throws AppUserNotFoundException, ModuleNotFoundException, SessionNotFoundException {
        Long userId = requestDataContext.getUserId();
        log.info("User id: {}", userId);
        ModuleReadModel module = moduleFacade.createModule(userId, NEW_MODULE);
        SessionWriteModel session = sessionFacade.createSession(userId, NEW_MODULE);
        sessionFacade.addModuleToSession(userId,session.getId(), module.getId());
        return moduleFacade.getModulesByUserId(userId);
    }

    public void deleteModule(){
        Long userId = requestDataContext.getUserId();
        Long moduleId = requestDataContext.getModuleId();
        List<SessionWriteModel> sessions = sessionFacade.getSessionsForUserAndModule(userId, moduleId);
        for (SessionWriteModel session: sessions) {
            sessionFacade.deleteSession(userId, session.getId());
        }
        sentenceFacade.rearrangeSetByModuleId(userId, moduleId,new ArrayList<SentenceWriteModel>());
        moduleFacade.deleteModule(moduleId, userId);
    }

    public ModuleReadModel updateModuleWithSessionName(ModuleWriteModel toUpdate) throws ModuleNotFoundException {
        Long userId = requestDataContext.getUserId();
        sessionFacade.updateSessionName(userId, toUpdate);
        return moduleFacade.updateModule(userId, toUpdate);
    }

    private List<SentenceReadModel> createSentencesFromProperties(Long userId, Long moduleId) throws SentenceNotFoundException {
        List<String> sentences = sentencesProperties.polish();
        if (sentences == null || sentences.isEmpty()) {
            throw new IllegalArgumentException("Lista zdań nie może być pusta.");
        }
        return sentenceFacade.createMany(userId, moduleId, sentences);
    }
}
