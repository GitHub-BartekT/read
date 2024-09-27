package pl.iseebugs.doread.domain.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByUserId(Long userId);
    void deleteByUserIdAndId(Long userId, Long  sessionId);
}