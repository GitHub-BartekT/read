package pl.iseebugs.doread.domain.module;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.module.dto.ModuleWriteModel;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

@AllArgsConstructor
@Service
public class ModuleFacade {

    AppUserFacade appUserFacade;

    public ModuleReadModel createModule (Long userId, ModuleWriteModel toCreate) throws AppUserNotFoundException {
        if (toCreate.getModuleName() == null || toCreate.getModuleName().isEmpty()) {
            toCreate.setModuleName("New module");
        }

        if(userId < 1){
            throw new IllegalArgumentException("User id is negative.");
        }

        AppUserReadModel user = appUserFacade.findUserById(userId);

        Module toSave = Module.builder()
                .moduleName(toCreate.getModuleName())
                .userId(user.id())
                .sessionsPerDay(3)
                .presentationsPerSession(5)
                .actualDay(1)
                .nextSession(1)
                .isPrivate(true)
                .build();

        return ModuleMapper.toReadModel(toSave);
    }
}
