package pl.iseebugs.doread.domain.module;

import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;

class ModuleMapper {

    public static ModuleReadModel toReadModel(Module module){
        return ModuleReadModel.builder()
                .id(module.getId())
                .userId(module.getUserId())
                .moduleName(module.getModuleName())
                .sessionsPerDay(module.getSessionsPerDay())
                .presentationsPerSession(module.getPresentationsPerSession())
                .newSentencesPerDay(module.getNewSentencesPerDay())
                .actualDay(module.getActualDay())
                .nextSession(module.getNextSession())
                .isPrivate(module.isPrivate())
                .build();
    }
}
