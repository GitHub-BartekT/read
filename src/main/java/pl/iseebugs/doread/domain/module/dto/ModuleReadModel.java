package pl.iseebugs.doread.domain.module.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuleReadModel {
    private Long id;
    private Long userId;
    private String moduleName;
    private int sessionsPerDay;
    private int presentationsPerSession;
    private int newSentencesPerDay;
    private int actualDay;
    private int nextSession;
    private boolean isPrivate;
}
