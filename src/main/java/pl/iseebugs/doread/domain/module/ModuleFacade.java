package pl.iseebugs.doread.domain.module;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.module.dto.ModuleWriteModel;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Service
public class ModuleFacade {

    private final AppUserFacade appUserFacade;
    private final ModuleRepository moduleRepository;
    private final ModuleValidator moduleValidator;

    public ModuleReadModel createModule(Long userId, String moduleName) throws AppUserNotFoundException {
        moduleName = moduleValidator.validateAndSetDefaultModuleName(moduleName);
        validateUserId(userId);

        AppUserReadModel user = appUserFacade.findUserById(userId);

        Module toSave = buildNewModule(moduleName, user);
        Module saved = moduleRepository.save(toSave);

        return ModuleMapper.toReadModel(saved);
    }

    public List<ModuleReadModel> getModulesByUserId(Long userId) {
        validateUserId(userId);

        return moduleRepository.findAllByUserId(userId)
                .stream()
                .map(ModuleMapper::toReadModel)
                .collect(Collectors.toList());
    }

    public List<ModuleReadModel> getPublicModules() {
        return moduleRepository.findAllByIsPrivateFalse()
                .stream()
                .map(ModuleMapper::toReadModel)
                .collect(Collectors.toList());
    }

    public ModuleReadModel getModuleByUserIdAndModuleId(Long userId, Long moduleId) throws ModuleNotFoundException {
        userAndModuleIdsValidator(userId, moduleId);

        return moduleRepository.findByIdAndUserId(moduleId, userId)
                .map(ModuleMapper::toReadModel)
                .orElseThrow(ModuleNotFoundException::new);
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
        log.info("updateModule: User id:{}, module id: {}", userId, toUpdate.getId());
        userAndModuleIdsValidator(userId, toUpdate.getId());

        Module entity = moduleRepository.findByIdAndUserId(toUpdate.getId(), userId)
                .orElseThrow(ModuleNotFoundException::new);

        if(moduleValidator.stringValidator(toUpdate.getModuleName())){
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

    @Transactional
    public void deleteModule(Long moduleId, Long userId) {
        log.info("Class: {}, method: {}", this.getClass().getSimpleName(), "deleteByIdAndUserId");
        log.info("Class: {}, method: {}", this.getClass().getSimpleName(), "deleteByIdAndUserId");
        moduleRepository.deleteByIdAndUserId(moduleId, userId);
    }

    private void validateUserId(Long userId) {
        moduleValidator.longValidator(userId, "Invalid User ID.");
    }

    private void userAndModuleIdsValidator(final Long userId, final Long moduleId) {
        moduleValidator.longValidator(userId, "User id is invalid.");
        moduleValidator.longValidator(moduleId, "Module id is invalid.");
    }

    private static Module buildNewModule(final String moduleName, final AppUserReadModel user) {
        return Module.builder()
                .moduleName(moduleName)
                .userId(user.id())
                .sessionsPerDay(3)
                .presentationsPerSession(5)
                .newSentencesPerDay(1)
                .actualDay(1)
                .nextSession(1)
                .isPrivate(true)
                .build();
    }
}
