package pl.iseebugs.doread.domain.session.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionModuleDto {
    private Long id;
    private Long sessionId;
    private Long moduleId;
    private Long ordinalPosition;
}
