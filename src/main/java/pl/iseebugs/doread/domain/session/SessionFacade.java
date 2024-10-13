package pl.iseebugs.doread.domain.session;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.domain.module.ModuleFacade;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.module.dto.ModuleWriteModel;
import pl.iseebugs.doread.domain.sentence.SentenceFacade;
import pl.iseebugs.doread.domain.sentence.dto.SentenceReadModel;
import pl.iseebugs.doread.domain.session.dto.SessionWriteModel;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class SessionFacade {

    private final SessionRepository sessionRepository;
    private final ModuleFacade moduleFacade;
    private final AppUserFacade appUserFacade;
    private final SentenceFacade sentenceFacade;

    public SessionWriteModel createSession(Long userId, String sessionName) throws AppUserNotFoundException {
        Session session = new Session();

        AppUserReadModel user = appUserFacade.findUserById(userId);
        session.setUserId(user.id());

        if (sessionName == null || sessionName.isBlank()) {
            session.setName("New session");
        } else {
            session.setName(sessionName);
        }

        session.setOrdinalType(OrdinalType.QUEUE);
        session.setOrdinalSchema("1");
        session.setSessionModules(new ArrayList<>());

        Session result = sessionRepository.save(session);
        return SessionMapper.toDto(result);
    }

    public void addModuleToSession(Long userId, Long sessionId, Long moduleId) throws AppUserNotFoundException, SessionNotFoundException, ModuleNotFoundException {
        AppUserReadModel user = appUserFacade.findUserById(userId);

        Session userSession = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(SessionNotFoundException::new);

        ModuleReadModel userModule = moduleFacade.getModuleByUserIdAndModuleId(userId, moduleId);

        Long maxOrdinalPosition = userSession.getSessionModules().stream()
                .map(SessionModule::getOrdinalPosition)
                .max(Long::compareTo)
                .orElse(0L);

        SessionModule newSession = SessionModule.builder()
                .moduleId(moduleId)
                .session(userSession)
                .ordinalPosition(maxOrdinalPosition + 1L)
                .build();

        userSession.getSessionModules().add(newSession);

        sessionRepository.save(userSession);
    }

    public List<SessionWriteModel> findAllSessionsByUserId(Long userId) {
        return sessionRepository.findAllByUserId(userId).stream()
                .map(SessionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SessionWriteModel> getSessionsForUserAndModule(Long userId, Long moduleId) {
        return sessionRepository.findSessionsByUserIdAndModuleIdWithSingleModule(userId, moduleId).stream()
                .map(SessionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<String> getNextSession(Long userId, Long sessionId) throws AppUserNotFoundException, SessionNotFoundException, ModuleNotFoundException {
        AppUserReadModel user = appUserFacade.findUserById(userId);

        Session userSession = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(SessionNotFoundException::new);

        List<SessionModule> modules = userSession.getSessionModules();
        List<String> nextSessionSentences = new ArrayList<>();
        Map<Long, List<String>> session = new HashMap<>();

        for (SessionModule sessionModule : modules) {
            ModuleReadModel module = moduleFacade.getModuleByUserIdAndModuleId(userId, sessionModule.getModuleId());
            long firstSentence = module.getActualDay();
            long lastSentence = module.getPresentationsPerSession() + firstSentence - 1L;
            List<SentenceReadModel> sentences = sentenceFacade.findAllByModuleIdAndBetween(
                    userId,
                    module.getId(),
                    firstSentence,
                    lastSentence
            );

            List<String> sentenceStrings = sentences.stream()
                    .map(SentenceReadModel::getSentence)
                    .toList();

            session.put(module.getId(), sentenceStrings);
        }

        for (SessionModule sessionModule : modules) {
            nextSessionSentences.addAll(session.get(sessionModule.getModuleId()));
        }

        nextSessionSentences.forEach(System.out::println);

        return nextSessionSentences;
    }

    public void endSession(Long userId, Long sessionId) throws AppUserNotFoundException, SessionNotFoundException, ModuleNotFoundException {
        AppUserReadModel user = appUserFacade.findUserById(userId);

        Session userSession = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(SessionNotFoundException::new);

        List<SessionModule> modules = userSession.getSessionModules();

        for (SessionModule sessionModule : modules) {
            ModuleReadModel module = moduleFacade.getModuleByUserIdAndModuleId(userId, sessionModule.getModuleId());
            moduleFacade.setNextSession(userId, module.getId());
        }
    }

    @Transactional
    public void deleteSession(Long userId, Long sessionId) {
        sessionRepository.deleteByUserIdAndId(userId, sessionId);
    }

    @Transactional
    public void updateSessionName(Long userId, ModuleWriteModel toUpdate) throws ModuleNotFoundException {
        List<Session> userSessions = sessionRepository.findSessionsByUserIdAndModuleIdWithSingleModule(userId, toUpdate.getId());
        log.info("Session size: {}", userSessions.size());

        if(userSessions.size() == 1) {
            Session entity = sessionRepository.findByIdAndUserId(userSessions.get(0).getId(), userId)
                    .orElseThrow(ModuleNotFoundException::new);
            log.info("Session id: {}", entity.getId());

            if (stringValidator(toUpdate.getModuleName())) {
                entity.setName(toUpdate.getModuleName());
                log.info("Name valid: {}", toUpdate.getModuleName());
            }
            Session updated = sessionRepository.save(entity);
            log.info("Session updated id: {}, name: {}", updated.getId(), updated.getName());

        }
    }

    boolean stringValidator(String argument) {
        return argument != null && !argument.isBlank();
    }
}

