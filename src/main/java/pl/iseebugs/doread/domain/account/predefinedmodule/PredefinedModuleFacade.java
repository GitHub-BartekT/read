package pl.iseebugs.doread.domain.account.predefinedmodule;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.module.ModuleFacade;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.sentence.SentenceFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;

@Service
@AllArgsConstructor
public class PredefinedModuleFacade {

    private final ModuleFacade moduleFacade;
    private final SentenceFacade sentenceFacade;

    public void creatingPredefinedModule(Long userId) throws AppUserNotFoundException {
        ModuleReadModel module = moduleFacade.createModule(userId, "Język polski");
        sentenceFacade.createSentencesFromProperties(userId, module.getId());
    }
}
