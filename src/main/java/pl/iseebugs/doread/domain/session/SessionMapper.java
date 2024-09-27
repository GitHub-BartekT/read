package pl.iseebugs.doread.domain.session;

import pl.iseebugs.doread.domain.session.Session;
import pl.iseebugs.doread.domain.session.SessionModule;
import pl.iseebugs.doread.domain.session.dto.SessionDto;
import pl.iseebugs.doread.domain.session.dto.SessionModuleDto;

import java.util.List;
import java.util.stream.Collectors;

public class SessionMapper {

    public static SessionDto toDto(Session session) {
        List<SessionModuleDto> moduleDtos = session.getSessionModules().stream()
                .map(SessionModuleMapper::toDto)
                .collect(Collectors.toList());

        return SessionDto.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .groupName(session.getGroupName())
                .ordinalType(session.getOrdinalType())
                .ordinalSchema(session.getOrdinalSchema())
                .sessionModules(moduleDtos)
                .build();
    }

    public static Session toEntity(SessionDto sessionDto) {
        List<SessionModule> modules = sessionDto.getSessionModules().stream()
                .map(SessionModuleMapper::toEntity)
                .collect(Collectors.toList());

        Session session = Session.builder()
                .id(sessionDto.getId())
                .userId(sessionDto.getUserId())
                .groupName(sessionDto.getGroupName())
                .ordinalType(sessionDto.getOrdinalType())
                .ordinalSchema(sessionDto.getOrdinalSchema())
                .build();

        session.setSessionModules(modules);
        modules.forEach(module -> module.setSessionId(session.getId()));
        return session;
    }
}
