package pl.iseebugs.doread.domain.sentence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SentenceRepository extends JpaRepository<Sentence, Long> {
    List<Sentence> findByModuleId(Long moduleId);
    Page<Sentence> findByModuleIdOrderByOrdinalNumberAsc(Long moduleId, Pageable pageable);
}
