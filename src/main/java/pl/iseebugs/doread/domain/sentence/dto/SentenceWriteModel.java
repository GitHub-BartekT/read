package pl.iseebugs.doread.domain.sentence.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentenceWriteModel {
    private Long id;
    private Long moduleId;
    private Long userId;
    private Long ordinalNumber;
    private String sentence;
}
