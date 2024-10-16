package pl.iseebugs.doread.domain.modulesessioncoordinator;

import lombok.AllArgsConstructor;
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

import java.util.List;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class ModuleSessionCoordinatorFacade {

    private final String MODULE_NAME = "Język polski";
    private final String NEW_MODULE = "Nowy moduł";

    private final ModuleFacade moduleFacade;
    private final SessionFacade sessionFacade;
    private final SentencesProperties sentencesProperties;
    private final SentenceFacade sentenceFacade;

    public void creatingPredefinedModule(Long userId) throws AppUserNotFoundException, ModuleNotFoundException, SessionNotFoundException, SentenceNotFoundException {
        ModuleReadModel module = moduleFacade.createModule(userId, MODULE_NAME);
        createSentencesFromProperties(userId, module.getId());
        SessionWriteModel session = sessionFacade.createSession(userId, MODULE_NAME);
        sessionFacade.addModuleToSession(userId,session.getId(), module.getId());
    }

    public List<ModuleReadModel> createNewModule(Long userId) throws AppUserNotFoundException, ModuleNotFoundException, SessionNotFoundException {
        ModuleReadModel module = moduleFacade.createModule(userId, NEW_MODULE);
        SessionWriteModel session = sessionFacade.createSession(userId, NEW_MODULE);
        sessionFacade.addModuleToSession(userId,session.getId(), module.getId());
        return moduleFacade.getModulesByUserId(userId);
    }

    public void deleteModule(Long userId, Long moduleId){
        List<SessionWriteModel> sessions = sessionFacade.getSessionsForUserAndModule(userId, moduleId);
        for (SessionWriteModel session: sessions) {
            sessionFacade.deleteSession(userId, session.getId());
        }
        moduleFacade.deleteModule(moduleId, userId);
    }

    public ModuleReadModel updateModuleWithSessionName(Long userId, ModuleWriteModel toUpdate) throws ModuleNotFoundException {
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
