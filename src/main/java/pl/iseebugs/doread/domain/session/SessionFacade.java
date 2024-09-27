package pl.iseebugs.doread.domain.session;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.session.dto.SessionWriteModel;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SessionFacade {

    private final SessionRepository sessionRepository;
    private final AppUserFacade appUserFacade;

    public SessionWriteModel createSession(Long userId, SessionWriteModel sessionWriteModel) throws AppUserNotFoundException {
        Session session = new Session();

        AppUserReadModel user = appUserFacade.findUserById(userId);
        session.setUserId(user.id());

        if (sessionWriteModel.getName() == null || sessionWriteModel.getName().isBlank()){
            session.setName("New session");
        } else {
            session.setName(session.getName());
        }

        session.setOrdinalType(OrdinalType.QUEUE);
        session.setOrdinalSchema("1");
        Session result = sessionRepository.save(session);
        return SessionMapper.toDto(result);
    }

    public List<SessionWriteModel> findAllSessionsByUserId(Long userId) {
        return sessionRepository.findAllByUserId(userId).stream()
                .map(SessionMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteSession(Long userId, Long sessionId) {
        sessionRepository.deleteByUserIdAndId(userId, sessionId);
    }
}
