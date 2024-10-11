package pl.iseebugs.doread.domain.sentence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SentenceRepository extends JpaRepository<Sentence, Long> {
    List<Sentence> findByUserIdAndModuleId(Long userId, Long moduleId);
    Page<Sentence> findByModuleIdOrderByOrdinalNumberAsc(Long moduleId, Pageable pageable);
    List<Sentence> findByUserIdAndModuleIdAndOrdinalNumberBetweenOrderByOrdinalNumberAsc(
            Long userId,
            Long moduleId,
            Long startOrdinalNumber,
            Long endOrdinalNumber
    );

    void deleteByUserIdAndModuleIdAndOrdinalNumber(Long userid, Long moduleId, Long id);
}
