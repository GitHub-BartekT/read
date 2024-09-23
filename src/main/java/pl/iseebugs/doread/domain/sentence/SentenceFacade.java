package pl.iseebugs.doread.domain.sentence;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.domain.sentence.dto.SentenceReadModel;
import pl.iseebugs.doread.domain.sentence.dto.SentenceWriteModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SentenceFacade {

    SentenceRepository sentenceRepository;

    public List<SentenceReadModel> findAllByModuleId(Long moduleId) {
        return sentenceRepository.findByModuleId(moduleId).stream()
                .map(SentenceMapper::toReadModel)
                .toList();
    }

    public SentenceReadModel findById(Long id) {
        return sentenceRepository.findById(id).map(SentenceMapper::toReadModel)
                .orElseThrow(() -> new IllegalArgumentException("Sentence with id " + id + " not found"));
    }
    
    public Page<SentenceReadModel> findByModuleIdWithPagination(Long moduleId, int page, int size) {
        return sentenceRepository.findByModuleIdOrderByOrdinalNumberAsc(moduleId, PageRequest.of(page, size))
                .map(SentenceMapper::toReadModel);
    }

    @Transactional
    public SentenceReadModel create(Long moduleId, String sentenceText) {
        if (sentenceText == null || sentenceText.isEmpty()) {
            throw new IllegalArgumentException("Sentence text must not be null or empty");
        }
        Sentence sentence = Sentence.builder()
                .moduleId(moduleId)
                .sentence(sentenceText)
                .build();
        Sentence result = sentenceRepository.save(sentence);
        return SentenceMapper.toReadModel(result);
    }

    @Transactional
    public List<SentenceReadModel> replaceSetByModuleId(Long moduleId, List<SentenceWriteModel> inserts) {
        List<Sentence> existingSentences = sentenceRepository.findByModuleId(moduleId);

        List<Long> newIds = inserts.stream()
                .map(SentenceWriteModel::getId)
                .filter(Objects::nonNull)
                .toList();

        existingSentences.stream()
                .filter(sentence -> !newIds.contains(sentence.getId()))
                .forEach(sentence -> sentenceRepository.delete(sentence));

        for (int i = 0; i < inserts.size(); i++) {
            SentenceWriteModel insert = inserts.get(i);
            Sentence sentence;

            if (insert.getId() != null) {
                sentence = existingSentences.stream()
                        .filter(s -> s.getId().equals(insert.getId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Zdanie o ID " + insert.getId() + " nie istnieje."));
            } else {
                sentence = new Sentence();
                sentence.setModuleId(moduleId);
            }

            sentence.setSentence(insert.getSentence());
            sentence.setOrdinalNumber((long) (i + 1));
            sentenceRepository.save(sentence);
        }
        return findAllByModuleId(moduleId);
    }

    @Transactional
    public void deleteById(Long id) {
        sentenceRepository.deleteById(id);
    }

    @Transactional
    public SentenceReadModel updateById(Long id, String newSentence) {
        SentenceReadModel sentence = findById(id);
        Sentence toUpdate = SentenceMapper.toEntity(sentence);
        toUpdate.setSentence(newSentence);
        Sentence result = sentenceRepository.save(toUpdate);
        return SentenceMapper.toReadModel(result);
    }
}

