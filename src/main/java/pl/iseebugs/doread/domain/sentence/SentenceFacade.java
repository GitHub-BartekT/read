package pl.iseebugs.doread.domain.sentence;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.domain.sentence.dto.SentenceWriteModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SentenceFacade {

    SentenceRepository sentenceRepository;

    public List<Sentence> findAllByModuleId(Long moduleId) {
        return sentenceRepository.findByModuleId(moduleId);
    }

    public Sentence findById(Long id) {
        return sentenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sentence with id " + id + " not found"));
    }

    public Page<Sentence> findByModuleIdWithPagination(Long moduleId, int page, int size) {
        return sentenceRepository.findByModuleIdOrderByOrdinalNumberAsc(moduleId, PageRequest.of(page, size));
    }

    @Transactional
    public Sentence create(Long moduleId, String sentenceText) {
        if (sentenceText == null || sentenceText.isEmpty()) {
            throw new IllegalArgumentException("Sentence text must not be null or empty");
        }
        Sentence sentence = Sentence.builder()
                .moduleId(moduleId)
                .sentence(sentenceText)
                .build();
        return sentenceRepository.save(sentence);
    }

    @Transactional
    public void replaceSetByModuleId(Long moduleId, List<SentenceWriteModel> inserts) {
        List<Sentence> existingSentences = sentenceRepository.findByModuleId(moduleId);

        List<Long> newIds = inserts.stream()
                .map(SentenceWriteModel::getId)
                .filter(Objects::nonNull)
                .toList();

        existingSentences.stream()
                .filter(sentence -> !newIds.contains(sentence.getId()))
                .forEach(sentence -> sentenceRepository.delete(sentence));

        //TODO:
        // Zajmujemy się dodawaniem i aktualizowaniem zdań
        for (int i = 0; i < inserts.size(); i++) {
            SentenceWriteModel insert = inserts.get(i);
            Sentence sentence;

            if (insert.getId() != null) {
                // Znajdujemy istniejące zdanie, jeśli ma ID
                sentence = existingSentences.stream()
                        .filter(s -> s.getId().equals(insert.getId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Zdanie o ID " + insert.getId() + " nie istnieje."));
            } else {
                // Tworzymy nowe zdanie, jeśli ID jest null
                sentence = new Sentence();
                sentence.setModuleId(moduleId);
            }

            // Aktualizujemy dane zdania
            sentence.setSentence(insert.getSentence());
            sentence.setOrdinalNumber((long) (i + 1));  // Ustawiamy numer porządkowy na podstawie pozycji w liście
            sentenceRepository.save(sentence);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        sentenceRepository.deleteById(id);
    }

    @Transactional
    public Sentence updateById(Long id, String newSentence) {
        Sentence sentence = findById(id);
        sentence.setSentence(newSentence);
        return sentenceRepository.save(sentence);
    }
    //ordinal numbers ma się zachowywać jak List jeżeli mamy 3 sentence o ordinalNumber= 1,2,3 i dodajemy kolejny o numerze 2 to numer 2 zmienia sie na 3, a 3 na 4.

    //insertSentence (exactly that same like in a list(jak opis wyżej)
    // SomeInsert()
    // {
    //     long id;
    //     String sentence;
    // }
    //replaceSetByModulId(Long moduleId, List<SomeInsert> argument);
    // w bazie danych mamy (to są dane dla jednego moduleId który nas interesuje)
    // w każdym też jest moduleId oraz sentence
    // dane niech się kończą na id=31 np.
    // id 1, ordinalNumber = 1
    // id 3, ordinalNumber = 2
    // id 18, ordinalNumber = 4
    // id 25, ordinalNumber = 3
    //
    // jako "argument" przekazujemy
    // (w każdym też jest sentence)
    // id 1,
    // id 18,
    // (bez podanego id)
    // id 25,
    // id 3
    //
    //    w wyniku powinny operacji powinniśmy nadpisać ordinal id dla istniejących danych zgodnie z kolejnością w List, oraz stworzyć dane jezeli nie mają id
    // id 1, ordinalNumber = 1
    // id 3, ordinalNumber = 5
    // id 18, ordinalNumber = 2
    // id 25, ordinalNumber = 4
    // id 32, ordinalNymber =3 (new data)

}

