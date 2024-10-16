package pl.iseebugs.doread.domain.module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.iseebugs.doread.domain.module.dto.ModuleWriteModel;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

class ModuleValidationTest {

    private ModuleFacade moduleFacade;

    @BeforeEach
    void setUp() {
        var mockAppUserFacade = mock(AppUserFacade.class);
        var mockModuleRepository = mock(ModuleRepository.class);

        moduleFacade = new ModuleFacade(
                mockAppUserFacade,
                mockModuleRepository,
                new ModuleValidator()
        );
    }


    @Test
    @DisplayName("getModuleByUserIdAndModuleId should throws ModuleNotFoundException")
    void getModuleByUserIdAndModuleId_throws_ModuleNotFoundException() {
        // Given + When
        Throwable e = catchThrowable(() -> moduleFacade.getModuleByUserIdAndModuleId(1L, 1L));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(ModuleNotFoundException.class)
        );
    }

    @Test
    @DisplayName("createModule should throws IllegalArgumentException \"Invalid User ID.\"")
    void createModule_throws_IllegalArgumentException() {
        // Given + When
        Throwable e = catchThrowable(() -> moduleFacade.createModule(-1L, "foo"));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid User ID.")
        );
    }

    @Test
    @DisplayName("createModule should throws IllegalArgumentException \"Invalid User ID.\"")
    void updateModule_throws_IllegalArgumentException_when_invalid_user_id() {
        // Given
        Long userId = -1L;
        ModuleWriteModel moduleToUpdate = new ModuleWriteModel();
        moduleToUpdate.setId(1L);

        // When
        Throwable e = catchThrowable(() -> moduleFacade.updateModule(userId, moduleToUpdate));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid User ID.")
        );
    }

    @Test
    @DisplayName("createModule should throws IllegalArgumentException \"Invalid module id.\"")
    void updateModule_throws_IllegalArgumentException_when_invalid_module_id() {
        // Given
        Long userId = 1L;

        ModuleWriteModel moduleToUpdate = new ModuleWriteModel();
        moduleToUpdate.setId(-1L);

        // When
        Throwable e = catchThrowable(() -> moduleFacade.updateModule(userId, moduleToUpdate));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid module id.")
        );
    }

    @Test
    @DisplayName("createModule should throws ModuleNotFoundException when no user")
    void updateModule_throws_ModuleNotFoundException() {
        // Given
        Long userId = 1L;

        ModuleWriteModel moduleToUpdate = new ModuleWriteModel();
        moduleToUpdate.setId(1L);
        // When
        Throwable e = catchThrowable(() -> moduleFacade.updateModule(userId, moduleToUpdate));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(ModuleNotFoundException.class)
        );
    }

    @Test
    @DisplayName("setNextSession should throws IllegalArgumentException \"Invalid User ID.\"")
    void setNextSession_throws_IllegalArgumentException_when_invalid_user_id() {
        // Given
        Long userId = -1L;
        Long moduleId = 1L;

        //When
        Throwable e = catchThrowable(() -> moduleFacade.setNextSession(userId, moduleId));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid User ID.")
        );
    }

    @Test
    @DisplayName("setNextSession should throws IllegalArgumentException \"Invalid module id.\"")
    void setNextSession_throws_IllegalArgumentException_when_invalid_module_id() {
        // Given
        Long userId = 1L;
        Long moduleId = -1L;

        //When
        Throwable e = catchThrowable(() -> moduleFacade.setNextSession(userId, moduleId));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid module id.")
        );
    }
}
