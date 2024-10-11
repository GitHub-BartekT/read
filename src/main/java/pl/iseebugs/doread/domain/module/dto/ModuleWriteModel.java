package pl.iseebugs.doread.domain.module.dto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuleWriteModel {
    private Long id;
    private Long userId;
    private String moduleName;
    private Integer sessionsPerDay;
    private Integer presentationsPerSession;
    private Integer newSentencesPerDay;
    private Integer actualDay;
    private Integer nextSession;
    private Boolean isPrivate;
}
