package pl.iseebugs.doread.domain.session;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.iseebugs.doread.domain.session.dto.SessionModuleDto;

@Component
@AllArgsConstructor
class SessionModuleMapper {

    SessionModuleRepository sessionModuleRepository;

    public static SessionModuleDto toDto(SessionModule sessionModule) {
        return SessionModuleDto.builder()
                .id(sessionModule.getId())
                .sessionId(sessionModule.getSession().getId())
                .moduleId(sessionModule.getModuleId())
                .ordinalPosition(sessionModule.getOrdinalPosition())
                .build();
    }

}
