package pl.iseebugs.doread.domain.session.dto;

import lombok.*;
import pl.iseebugs.doread.domain.session.OrdinalType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    private Long id;
    private Long userId;
    private String groupName;
    private OrdinalType ordinalType;
    private String ordinalSchema;
    private List<SessionModuleDto> sessionModules;  // Lista DTO dla module
}
