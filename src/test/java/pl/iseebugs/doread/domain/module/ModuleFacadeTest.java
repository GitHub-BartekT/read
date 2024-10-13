package pl.iseebugs.doread.domain.module;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.BaseIT;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ModuleFacadeTest extends BaseIT {

    @Autowired
    ModuleFacade moduleFacade;

    @Test
    void getModuleByUserIdAndModuleId() {
        // Given + When
        Throwable e = catchThrowable(() -> moduleFacade.getModuleByUserIdAndModuleId(1L, 1L));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(ModuleNotFoundException.class)
        );
    }
}