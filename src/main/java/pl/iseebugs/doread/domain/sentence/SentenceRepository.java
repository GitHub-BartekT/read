package pl.iseebugs.doread.domain.sentence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Repository
interface SentenceRepository extends JpaRepository<Sentence, Long> {
    Optional<Sentence> findByUserIdAndId(Long userId, Long sentenceId);
    List<Sentence> findByUserIdAndModuleIdOrderByOrdinalNumberAsc(Long userId, Long moduleId);
    List<Sentence> findByUserIdAndModuleIdAndOrdinalNumberBetweenOrderByOrdinalNumberAsc(
            Long userId,
            Long moduleId,
            Long startOrdinalNumber,
            Long endOrdinalNumber
    );

    void deleteByUserIdAndModuleIdAndOrdinalNumber(Long userid, Long moduleId, Long id);
}
