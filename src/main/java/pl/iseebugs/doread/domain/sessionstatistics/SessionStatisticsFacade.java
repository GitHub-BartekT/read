package pl.iseebugs.doread.domain.sessionstatistics;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class SessionStatisticsFacade {

    private final SessionStatisticsRepository sessionStatisticsRepository;

    public SessionStatistics createSessionStatistic(Long userId, Long sessionId, int sentencesCount, boolean isFinished, String ordinalType, int modulesInSession) {
        SessionStatistics sessionStatistics = SessionStatistics.builder()
                .userId(userId)
                .sessionId(sessionId)
                .sentencesCount(sentencesCount)
                .isFinished(isFinished)
                .ordinalType(ordinalType)
                .modulesInSession(modulesInSession)
                .build();
        SessionStatistics result =  sessionStatisticsRepository.save(sessionStatistics);
log.info("Created statistic id : {}", result.getId());
        return result;
    }

    public List<SessionStatistics> getAllSessionStatistics() {
        return sessionStatisticsRepository.findAll();
    }

    public SessionStatistics getSessionStatisticsById(Long id) {
        return sessionStatisticsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Session statistics not found."));
    }

    public void deleteSessionStatistics(Long id) {
        sessionStatisticsRepository.deleteById(id);
    }
}
