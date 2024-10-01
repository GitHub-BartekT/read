package pl.iseebugs.doread.domain.module;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.module.dto.ModuleWriteModel;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ModuleFacade {

    private final AppUserFacade appUserFacade;
    private final ModuleRepository moduleRepository;


    public ModuleReadModel createModule (Long userId, String moduleName) throws AppUserNotFoundException {
        if (moduleName == null || moduleName.isEmpty()) {
            moduleName = "New module";
        }

        if(userId < 1){
            throw new IllegalArgumentException("User id is negative.");
        }

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

    public Optional<ModuleReadModel> findByIdAndUserId(Long id, Long userId) {
        return moduleRepository.findByIdAndUserId(id, userId)
                .map(ModuleMapper::toReadModel);
    }

    public void deleteByIdAndUserId(Long id, Long userId) {
        moduleRepository.deleteByIdAndUserId(id, userId);
    }
}
