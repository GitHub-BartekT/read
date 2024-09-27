package pl.iseebugs.doread.domain.session;

import pl.iseebugs.doread.domain.session.dto.SessionWriteModel;
import pl.iseebugs.doread.domain.session.dto.SessionModuleDto;

import java.util.List;
import java.util.stream.Collectors;

class SessionMapper {

    public static SessionWriteModel toDto(Session session) {
        List<SessionModuleDto> moduleDtos = session.getSessionModules().stream()
                .map(SessionModuleMapper::toDto)
                .collect(Collectors.toList());

        return SessionWriteModel.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .name(session.getName())
                .ordinalType(session.getOrdinalType())
                .ordinalSchema(session.getOrdinalSchema())
                .sessionModules(moduleDtos)
                .build();
    }

    public static Session toEntity(SessionWriteModel sessionWriteModel) {
        List<SessionModule> modules = sessionWriteModel.getSessionModules().stream()
                .map(SessionModuleMapper::toEntity)
                .collect(Collectors.toList());

        Session session = Session.builder()
                .id(sessionWriteModel.getId())
                .userId(sessionWriteModel.getUserId())
                .groupName(sessionWriteModel.getName())
                .ordinalType(sessionWriteModel.getOrdinalType())
                .ordinalSchema(sessionWriteModel.getOrdinalSchema())
                .build();

        session.setSessionModules(modules);
        modules.forEach(module -> module.setSessionId(session.getId()));
        return session;
    }
}
