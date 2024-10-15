package pl.iseebugs.doread.domain.sentence;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.domain.module.ModuleFacade;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.sentence.dto.SentenceReadModel;
import pl.iseebugs.doread.domain.sentence.dto.SentenceWriteModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class SentenceFacade {

    private final SentenceRepository sentenceRepository;
    private final SentencesProperties sentencesProperties;
    private final ModuleFacade moduleFacade;
    private final SentenceValidator sentenceValidator;

    /**
     * Get all sentences read models by module id and user id. In asc order by ordinalNumber.
     *
     * @author Bartlomiej Tucholski
     * @contact iseebugs.pl
     * @since 1.0
     */
    public List<SentenceReadModel> getAllByUserIdAndModuleId(Long userId, Long moduleId) {
        userIdAndModuleIdValidator(userId, moduleId);
        return sentenceRepository.findByUserIdAndModuleIdOrderByOrdinalNumberAsc(userId, moduleId).stream()
                .map(SentenceMapper::toReadModel)
                .toList();
    }

    /**
     * Get all sentences strings by module id and user id. In asc order by ordinalNumber.
     *
     * @author Bartlomiej Tucholski
     * @contact iseebugs.pl
     * @since 1.0
     */
    public List<String> getAllSentenceByModuleId(Long userId, Long moduleId) {
        return getAllByUserIdAndModuleId(userId, moduleId).stream()
                .map(SentenceReadModel::getSentence)
                .toList();
    }

    /**
     * Get all sentences by module id and user id.
     * Numbers from a closed interval in asc order by ordinal number.
     *
     * @author Bartlomiej Tucholski
     * @contact iseebugs.pl
     * @since 1.0
     */
    public List<SentenceReadModel> findAllByModuleIdAndBetween(Long userId, Long moduleId, Long startOrdinalNumber, Long endOrdinalNumber) {
        userIdAndModuleIdValidator(userId, moduleId);
        sentenceValidator.validateRange(startOrdinalNumber, endOrdinalNumber);
        return sentenceRepository.findByUserIdAndModuleIdAndOrdinalNumberBetweenOrderByOrdinalNumberAsc(
                        userId,
                        moduleId,
                        startOrdinalNumber,
                        endOrdinalNumber).stream()
                .map(SentenceMapper::toReadModel)
                .toList();
    }

    /**
     * Find sentence by user id and sentence id.
     *
     * @author Bartlomiej Tucholski
     * @contact iseebugs.pl
     * @since 1.0
     */
    public SentenceReadModel findByUserIdAndId(Long userId, Long sentenceId) throws SentenceNotFoundException {
        userIdAndSentenceIdValidator(userId, sentenceId);
        return sentenceRepository.findByUserIdAndId(userId, sentenceId).map(SentenceMapper::toReadModel)
                .orElseThrow(SentenceNotFoundException::new);
    }

    @Transactional
    public SentenceReadModel create(Long userId, Long moduleId, String sentenceText) {
        Long size = (long) getAllByUserIdAndModuleId(userId, moduleId).size();

        if (sentenceText == null || sentenceText.isEmpty()) {
            throw new IllegalArgumentException("Sentence text must not be null or empty");
        }
        Sentence sentence = Sentence.builder()
                .moduleId(moduleId)
                .sentence(sentenceText)
                .ordinalNumber(size + 1)
                .userId(userId)
                .build();
        Sentence result = sentenceRepository.save(sentence);

        List<SentenceWriteModel> moduleSentences = getAllByUserIdAndModuleId(userId, moduleId).stream()
                .map(SentenceMapper::toWriteModelFromReadModel)
                .toList();
        rearrangeSetByModuleId(userId, moduleId, moduleSentences);

        return SentenceMapper.toReadModel(result);
    }

    /**
     * Tworzy wiele zdań na podstawie listy, przypisując moduleId, userId oraz odpowiedni ordinalNumber.
     *
     * @param userId   ID użytkownika
     * @param moduleId ID modułu
     * @return Lista utworzonych zdań w postaci SentenceReadModel
     */
    @Transactional
    public List<SentenceReadModel> createSentencesFromProperties(Long userId, Long moduleId) {
        List<String> sentences = sentencesProperties.polish();
        if (sentences == null || sentences.isEmpty()) {
            throw new IllegalArgumentException("Lista zdań nie może być pusta.");
        }

        List<Sentence> sentenceEntities = IntStream.range(0, sentences.size())
                .mapToObj(i -> Sentence.builder()
                        .moduleId(moduleId)
                        .userId(userId)
                        .ordinalNumber((long) (i + 1))
                        .sentence(sentences.get(i))
                        .build())
                .toList();

        List<Sentence> savedSentences = sentenceRepository.saveAll(sentenceEntities);

        return savedSentences.stream()
                .map(SentenceMapper::toReadModel)
                .toList();
    }

    @Transactional
    public List<SentenceReadModel> rearrangeSetByModuleId(Long userId, Long moduleId, List<SentenceWriteModel> inserts) {
        List<Sentence> existingSentences = sentenceRepository.findByUserIdAndModuleIdOrderByOrdinalNumberAsc(userId, moduleId);

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
        return getAllByUserIdAndModuleId(userId, moduleId);
    }

    @Transactional
    public void deleteById(Long id) {
        sentenceRepository.deleteById(id);
    }

    @Transactional
    public void deleteByUserIdAndModuleIdAndId(Long userId, Long moduleId, Long id) throws ModuleNotFoundException {
        ModuleReadModel module = moduleFacade.getModuleByUserIdAndModuleId(userId, moduleId);
        sentenceRepository.deleteByUserIdAndModuleIdAndOrdinalNumber(userId, module.getId(), id);

        List<SentenceWriteModel> moduleSentences = getAllByUserIdAndModuleId(userId, moduleId).stream()
                .map(SentenceMapper::toWriteModelFromReadModel)
                .toList();
        rearrangeSetByModuleId(userId, moduleId, moduleSentences);
    }

    @Transactional
    public SentenceReadModel updateById(Long userId, Long id, String newSentence) throws SentenceNotFoundException {
        SentenceReadModel sentence = findByUserIdAndId(userId, id);
        Sentence toUpdate = SentenceMapper.toEntity(sentence);
        toUpdate.setSentence(newSentence);
        Sentence result = sentenceRepository.save(toUpdate);
        return SentenceMapper.toReadModel(result);
    }

    public Page<SentenceReadModel> findByModuleIdWithPagination(Long moduleId, int page, int size) {
        return sentenceRepository.findByModuleIdOrderByOrdinalNumberAsc(moduleId, PageRequest.of(page, size))
                .map(SentenceMapper::toReadModel);
    }

    private void validateUserId(Long userId) {
        sentenceValidator.longValidator(userId, "Invalid user id.");
    }

    private void userIdAndModuleIdValidator(final Long userId, final Long moduleId) {
        validateUserId(userId);
        sentenceValidator.longValidator(moduleId, "Invalid module id.");
    }

    private void userIdAndSentenceIdValidator(final Long userId, final Long sentenceId) {
        validateUserId(userId);
        sentenceValidator.longValidator(sentenceId, "Invalid sentence id.");
    }
}

