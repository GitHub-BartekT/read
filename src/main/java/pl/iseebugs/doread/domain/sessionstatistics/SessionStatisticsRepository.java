package pl.iseebugs.doread.domain.sessionstatistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionStatisticsRepository extends JpaRepository<SessionStatistics, Long> {
}
