package pl.iseebugs.doread.infrastructure.context;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;

@Component
@RequestScope
@Data
@Getter
@Setter
public class RequestDataContext {
    private Long userId;
    private Long sessionId;
    private Long moduleId;

    public RequestDataContext() {
        this.userId = null;
        this.sessionId = null;
        this.moduleId = null;
    }
}
