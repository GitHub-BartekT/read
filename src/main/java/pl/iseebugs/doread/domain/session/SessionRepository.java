package pl.iseebugs.doread.domain.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByIdAndUserId(Long sessionId, Long userId);
    List<Session> findAllByUserId(Long userId);
    void deleteByUserIdAndId(Long userId, Long  sessionId);

    @Query("SELECT s FROM Session s JOIN s.sessionModules sm WHERE s.userId = :userId AND sm.moduleId = :moduleId AND size(s.sessionModules) = 1")
    List<Session> findSessionsByUserIdAndModuleIdWithSingleModule(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

}