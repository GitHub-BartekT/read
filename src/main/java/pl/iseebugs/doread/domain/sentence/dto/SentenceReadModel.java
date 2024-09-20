package pl.iseebugs.doread.domain.sentence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SentenceReadModel {
    private Long id;
    private Long moduleId;
    private Long ordinalNumber;
    private String sentence;
}
