package pl.iseebugs.doread.domain.module;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.module.dto.ModuleWriteModel;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ModuleFacade {

    private final AppUserFacade appUserFacade;
    private final ModuleRepository moduleRepository;
    private final ModuleValidator moduleValidator;

    public ModuleReadModel createModule(Long userId, String moduleName) throws AppUserNotFoundException {
        if (!moduleValidator.stringValidator(moduleName)) {
            moduleName = "New module";
        }

        moduleValidator.longValidator(userId, "User id is invalid.");

        AppUserReadModel user = appUserFacade.findUserById(userId);

        Module toSave = Module.builder()
                .moduleName(moduleName)
                .userId(user.id())
                .sessionsPerDay(3)
                .presentationsPerSession(5)
                .newSentencesPerDay(1)
                .actualDay(1)
                .nextSession(1)
                .isPrivate(true)
                .build();

        Module saved = moduleRepository.save(toSave);

        return ModuleMapper.toReadModel(saved);
    }

    public List<ModuleReadModel> findAllByUserId(Long userId) {
        moduleValidator.longValidator(userId, "User id is invalid.");
        
        return moduleRepository.findAllByUserId(userId)
                .stream()
                .map(ModuleMapper::toReadModel)
                .collect(Collectors.toList());
    }

    public List<ModuleReadModel> findAllPublicModules() {
        return moduleRepository.findAllByIsPrivateFalse()
                .stream()
                .map(ModuleMapper::toReadModel)
                .collect(Collectors.toList());
    }
    
    

    public ModuleReadModel findByIdAndUserId(Long userId, Long moduleId) throws ModuleNotFoundException {
        userAndModuleIdsValidator(userId, moduleId);

        return moduleRepository.findByIdAndUserId(moduleId, userId)
                .map(ModuleMapper::toReadModel).orElseThrow(ModuleNotFoundException::new);
    }

    public void setNextSession(Long userId, Long moduleId) throws ModuleNotFoundException {
        userAndModuleIdsValidator(userId, moduleId);

        Module module = moduleRepository.findByIdAndUserId(moduleId, userId)
                .orElseThrow(ModuleNotFoundException::new);

        if (module.getNextSession() == module.getSessionsPerDay()) {
            module.setActualDay(module.getActualDay() + 1);
            module.setNextSession(1);
        } else {
            module.setNextSession(module.getNextSession() + 1);
        }
        moduleRepository.save(module);
    }

    public ModuleReadModel updateModule(Long userId, ModuleWriteModel toUpdate) throws ModuleNotFoundException {
        userAndModuleIdsValidator(userId, toUpdate.getId());

        Module entity = moduleRepository.findByIdAndUserId(userId, toUpdate.getId())
                .orElseThrow(ModuleNotFoundException::new);

        if(!moduleValidator.stringValidator(toUpdate.getModuleName())){
            entity.setModuleName(toUpdate.getModuleName());
        }
        if(moduleValidator.integerValidator(toUpdate.getSessionsPerDay())){
            entity.setSessionsPerDay(toUpdate.getSessionsPerDay());
        }
        if(moduleValidator.integerValidator(toUpdate.getPresentationsPerSession())){
            entity.setPresentationsPerSession(toUpdate.getPresentationsPerSession());
        }
        if(moduleValidator.integerValidator(toUpdate.getNewSentencesPerDay())){
            entity.setNewSentencesPerDay(toUpdate.getNewSentencesPerDay());
        }
        if(moduleValidator.integerValidator(toUpdate.getActualDay())){
            entity.setActualDay(toUpdate.getActualDay());
        }
        if(moduleValidator.integerValidator(toUpdate.getNextSession())){
            entity.setNextSession(toUpdate.getNextSession());
        }
        if(toUpdate.getIsPrivate() != null){
            entity.setPrivate(!toUpdate.getIsPrivate());
        }

        Module saved = moduleRepository.save(entity);

        return ModuleMapper.toReadModel(saved);
    }

    public void deleteByIdAndUserId(Long id, Long userId) {
        moduleRepository.deleteByIdAndUserId(id, userId);
    }

    private void userAndModuleIdsValidator(final Long userId, final Long moduleId) {
        moduleValidator.longValidator(userId, "User id is invalid.");
        moduleValidator.longValidator(moduleId, "Module id is invalid.");
    }
}
