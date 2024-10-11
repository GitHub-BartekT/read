package pl.iseebugs.doread.domain.session;

import pl.iseebugs.doread.domain.session.dto.SessionWriteModel;
import pl.iseebugs.doread.domain.session.dto.SessionModuleDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class SessionMapper {

    public static SessionWriteModel toDto(Session session) {
        List<SessionModuleDto> moduleDtos = session.getSessionModules() != null ?
                session.getSessionModules().stream()
                        .map(SessionModuleMapper::toDto)
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        return SessionWriteModel.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .name(session.getName())
                .ordinalType(session.getOrdinalType())
                .ordinalSchema(session.getOrdinalSchema())
                .sessionModules(moduleDtos)
                .build();
    }
}
