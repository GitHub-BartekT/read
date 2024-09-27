package pl.iseebugs.doread.domain.session.dto;

import lombok.*;
import pl.iseebugs.doread.domain.session.OrdinalType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionWriteModel {
    private Long id;
    private Long userId;
    private String name;
    private OrdinalType ordinalType;
    private String ordinalSchema;
    private List<SessionModuleDto> sessionModules;
}
