package pl.iseebugs.doread.domain.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SessionModuleRepository extends JpaRepository<SessionModule, Long> {
}
