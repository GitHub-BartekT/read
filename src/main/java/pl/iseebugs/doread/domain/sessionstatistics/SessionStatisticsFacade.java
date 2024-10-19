package pl.iseebugs.doread.domain.sessionstatistics;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SessionStatisticsFacade {

    private final SessionStatisticsRepository sessionStatisticsRepository;

    public SessionStatistics createSessionStatistic(Long userId, Long moduleId, int sentencesCount, boolean isFinished, String ordinalType, int modulesInSession) {
        SessionStatistics sessionStatistics = SessionStatistics.builder()
                .userId(userId)
                .moduleId(moduleId)
                .sentencesCount(sentencesCount)
                .isFinished(isFinished)
                .ordinalType(ordinalType)
                .modulesInSession(modulesInSession)
                .build();
        return sessionStatisticsRepository.save(sessionStatistics);
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
