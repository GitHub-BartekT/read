package pl.iseebugs.doread.domain.sentence;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.domain.sentence.dto.SentenceReadModel;
import pl.iseebugs.doread.domain.sentence.dto.SentenceWriteModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Log4j2
public class SentenceFacade {

    private final SentenceRepository sentenceRepository;
    private final SentenceValidator sentenceValidator;

    /**
     * Get all sentences read models by module id and user id. In asc order by ordinalNumber.
     *
     * @author Bartlomiej Tucholski
     * @contact iseebugs.pl
     * @since 1.0
     */
    public List<SentenceReadModel> getAllByUserIdAndModuleId(Long userId, Long moduleId) {
        sentenceValidator.userIdAndModuleIdValidator(userId, moduleId);
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
        sentenceValidator.userIdAndModuleIdValidator(userId, moduleId);
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
        sentenceValidator.userIdAndSentenceIdValidator(userId, sentenceId);
        return sentenceRepository.findByUserIdAndId(userId, sentenceId).map(SentenceMapper::toReadModel)
                .orElseThrow(SentenceNotFoundException::new);
    }

    /**
     * Create new sentence by user id and module id. Set ordinal number as max.ordinalNumber + 1 from current module.
     *
     * @author Bartlomiej Tucholski
     * @contact iseebugs.pl
     * @since 1.0
     */
    @Transactional
    public SentenceReadModel create(Long userId, Long moduleId, String sentenceText) {
        sentenceValidator.userIdAndModuleIdValidator(userId, moduleId);
        sentenceText = sentenceValidator.validateAndSetDefaultSentenceText(sentenceText);

        long size = getAllByUserIdAndModuleId(userId, moduleId).size();

        Sentence toSave = buildSentence(userId, moduleId, sentenceText, size);
        Sentence result = sentenceRepository.save(toSave);

        return SentenceMapper.toReadModel(result);
    }

    /**
     * Create new sentences by user id and module id.
     * Add set at the end of existing set. Set first new sentence ordinal number as current set size + 1.
     *
     * @author Bartlomiej Tucholski
     * @contact iseebugs.pl
     * @since 1.0
     */
    @Transactional
    public List<SentenceReadModel> createMany(Long userId, Long moduleId, List<String> listToSave) {
        sentenceValidator.userIdAndModuleIdValidator(userId, moduleId);

        List<SentenceReadModel> existSentences = getAllByUserIdAndModuleId(userId, moduleId);
        int startIndex = existSentences.size();

        List<Sentence> toSave = setListToSave(userId, moduleId, listToSave, startIndex);

        sentenceRepository.saveAll(toSave);

        return getAllByUserIdAndModuleId(userId, moduleId);
    }

    /**
     * Set list of sentences to save.
     *
     * @author Bartlomiej Tucholski
     * @contact iseebugs.pl
     * @since 1.0
     */
    private List<Sentence> setListToSave(final Long userId, final Long moduleId, final List<String> listToSave, final int startIndex) {
        List<Sentence> toSave = new ArrayList<>();
        for (int i = 0; i < listToSave.size() ; i++){
            String sentenceText = listToSave.get(i);
            String validateText = sentenceValidator.validateAndSetDefaultSentenceText(sentenceText);
            Sentence newSentence = Sentence.builder()
                    .sentence(validateText)
                    .moduleId(moduleId)
                    .userId(userId)
                    .ordinalNumber((long) (startIndex + i))
                    .build();
            toSave.add(newSentence);
        }
        return toSave;
    }

    /**
     * Crete sentence with increment by 1 ordinal number.
     *
     * @author Bartlomiej Tucholski
     * @contact iseebugs.pl
     * @since 1.0
     */
    private static Sentence buildSentence(final Long userId, final Long moduleId, final String sentenceText, final long size) {
        return Sentence.builder()
                .moduleId(moduleId)
                .sentence(sentenceText)
                .ordinalNumber(size + 1)
                .userId(userId)
                .build();
    }

    /**
     * Delete sentences by:
     * <ol>
     *     <li>User Id</li>
     *     <li>Module Id</li>
     *     <li>Ordinal Number</li>
     * </ol>
     *
     * After deleting sentence rebuild queue of module sentences ordinal numbers set.
     *
     * @author Bartlomiej Tucholski
     * @contact iseebugs.pl
     * @since 1.0
     */
    @Transactional
    public List<SentenceReadModel> deleteByUserIdAndModuleIdAndOrdinalNumber(Long userId, Long moduleId, Long ordinalNumber) {
        sentenceValidator.userIdAndModuleIdValidator(userId, moduleId);
        sentenceValidator.longValidator(ordinalNumber, "Invalid ordinalNumber");

        sentenceRepository.deleteByUserIdAndModuleIdAndOrdinalNumber(userId, moduleId, ordinalNumber);
        log.info("Deleted sentence: userId: {}, moduleId: {}, ordinalNumber: {}", userId, moduleId, ordinalNumber);
        return rearrangeSetAfterDeletedOneSentence(userId, moduleId);
    }

    /**
     * Rebuild sentences module ordinal numbers after deleting one sentence.
     *
     * @author Bartlomiej Tucholski
     * @contact iseebugs.pl
     * @since 1.0
     */
    private List<SentenceReadModel> rearrangeSetAfterDeletedOneSentence(Long userId, Long moduleId){
        List<Sentence> existingSentences = sentenceRepository.findByUserIdAndModuleIdOrderByOrdinalNumberAsc(userId, moduleId);
        Long ordinalNumber = 1L;

        for (Sentence sentence: existingSentences) {
            if(sentence.getOrdinalNumber() != ordinalNumber) {
                sentence.setOrdinalNumber(ordinalNumber);
            }
            ordinalNumber++;
        }

        return getAllByUserIdAndModuleId(userId, moduleId);
    }

    //TODO To tests. Removing all sentences from module.
    @Transactional
    public List<SentenceReadModel> rearrangeSetByModuleId(Long userId, Long moduleId, List<SentenceWriteModel> inserts) {
        List<Sentence> existingSentences = sentenceRepository.findByUserIdAndModuleIdOrderByOrdinalNumberAsc(userId, moduleId);

        List<Long> newIds = inserts.stream()
                .map(SentenceWriteModel::getId)
                .filter(Objects::nonNull)
                .toList();

        existingSentences.stream()
                .filter(sentence -> !newIds.contains(sentence.getId()))
                .forEach(sentence ->{
                    sentenceRepository.delete(sentence);
                    log.info("Deleted sentence: userId: {}, moduleId: {}, sentenceId: {}", userId, moduleId, sentence.getId());
                });

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
}

