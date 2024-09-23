package pl.iseebugs.doread.domain.sentence;

import pl.iseebugs.doread.domain.sentence.dto.SentenceReadModel;
import pl.iseebugs.doread.domain.sentence.dto.SentenceWriteModel;

class SentenceMapper {

    public static SentenceReadModel toReadModel(Sentence sentence){
        return SentenceReadModel.builder()
                .id(sentence.getId())
                .ordinalNumber(sentence.getOrdinalNumber())
                .moduleId(sentence.getModuleId())
                .userId(sentence.getUserId())
                .sentence(sentence.getSentence())
                .build();
    }

    public static Sentence toEntity(SentenceWriteModel sentence){
        return Sentence.builder()
                .id(sentence.getId())
                .ordinalNumber(sentence.getOrdinalNumber())
                .moduleId(sentence.getModuleId())
                .userId(sentence.getUserId())
                .sentence(sentence.getSentence())
                .build();
    }

    public static Sentence toEntity(SentenceReadModel sentence){
        return Sentence.builder()
                .id(sentence.getId())
                .ordinalNumber(sentence.getOrdinalNumber())
                .moduleId(sentence.getModuleId())
                .userId(sentence.getUserId())
                .sentence(sentence.getSentence())
                .build();
    }
}
