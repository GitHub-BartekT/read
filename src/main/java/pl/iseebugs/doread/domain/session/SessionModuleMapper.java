package pl.iseebugs.doread.domain.session;

import pl.iseebugs.doread.domain.session.dto.SessionModuleDto;

class SessionModuleMapper {

    public static SessionModuleDto toDto(SessionModule sessionModule) {
        return SessionModuleDto.builder()
                .id(sessionModule.getId())
                .sessionId(sessionModule.getSessionId())
                .moduleId(sessionModule.getModuleId())
                .ordinalPosition(sessionModule.getOrdinalPosition())
                .build();
    }

    public static SessionModule toEntity(SessionModuleDto sessionModuleDto) {
        return SessionModule.builder()
                .id(sessionModuleDto.getId())
                .sessionId(sessionModuleDto.getSessionId())
                .moduleId(sessionModuleDto.getModuleId())
                .ordinalPosition(sessionModuleDto.getOrdinalPosition())
                .build();
    }
}
