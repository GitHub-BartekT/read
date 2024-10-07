package pl.iseebugs.doread.domain.predefinedmodule;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.module.ModuleFacade;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.sentence.SentenceFacade;
import pl.iseebugs.doread.domain.session.SessionFacade;
import pl.iseebugs.doread.domain.session.SessionNotFoundException;
import pl.iseebugs.doread.domain.session.dto.SessionWriteModel;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;

@Service
@AllArgsConstructor
public class PredefinedModuleFacade {

    private final String MODULE_NAME = "Język polski";

    private final ModuleFacade moduleFacade;
    private final SentenceFacade sentenceFacade;
    private final SessionFacade sessionFacade;

    public void creatingPredefinedModule(Long userId) throws AppUserNotFoundException, ModuleNotFoundException, SessionNotFoundException {
        ModuleReadModel module = moduleFacade.create(userId, MODULE_NAME);
        sentenceFacade.createSentencesFromProperties(userId, module.getId());
        SessionWriteModel session = sessionFacade.createSession(userId, MODULE_NAME);
        sessionFacade.addModuleToSession(userId,session.getId(), module.getId());
    }
}
