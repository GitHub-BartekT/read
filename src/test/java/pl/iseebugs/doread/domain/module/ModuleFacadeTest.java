package pl.iseebugs.doread.domain.module;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.BaseIT;
import pl.iseebugs.doread.domain.account.delete.AccountDeleteFacade;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.module.dto.ModuleWriteModel;
import pl.iseebugs.doread.domain.security.projection.LoginTokenDto;
import pl.iseebugs.doread.domain.user.AppUser;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@Transactional
class ModuleFacadeTest extends BaseIT {

    @Autowired
    ModuleFacade moduleFacade;

    @Autowired
    AppUserFacade appUserFacade;

    @Autowired
    AccountDeleteFacade accountDeleteFacade;

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
    @DisplayName("getModuleByUserIdAndModuleId should returns data")
    void getModuleByUserIdAndModuleId_returns_ModuleReadModel() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        String moduleName = "fooModule";
        ModuleReadModel savedModule = moduleFacade.createModule(userId, moduleName);

        // + When
        ModuleReadModel result = moduleFacade.getModuleByUserIdAndModuleId(userId, savedModule.getId());

        // Then
        assertAll(
                () -> assertThat(result.getModuleName()).isEqualTo(moduleName)
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }

    @Test
    @DisplayName("getModulesByUserId should returns empty list when argument is non-exist user id")
    void getModulesByUserId_returns_Empty_List_when_no_user() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();
        createTestModules(userId, 10);

        Long nonExistUserId = 10000000L;

        // + When
        List<ModuleReadModel> result = moduleFacade.getModulesByUserId(nonExistUserId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0)
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }

    @Test
    @DisplayName("getModulesByUserId should returns empty list when no modules in database")
    void getModulesByUserId_returns_Empty_List_when_no_modules() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        Long moduleId = 1L;

        // + When
        List<ModuleReadModel> result = moduleFacade.getModulesByUserId(moduleId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0)
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }

    @Test
    @DisplayName("getModulesByUserId should returns empty list when module id is deletedModuleId")
    void getModulesByUserId_returns_Empty_List_when_module_was_deleted() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel savedModule = moduleFacade.createModule(userId, "foo");
        Long moduleId = savedModule.getId();
        deleteAllUserModules(userId);

        // + When
        List<ModuleReadModel> result = moduleFacade.getModulesByUserId(moduleId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0)
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }

    @Test
    @DisplayName("getModulesByUserId should returns list of modules")
    void getModulesByUserId_returns_List_of_modules() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        createTestModules(userId, 5);

        // + When
        List<ModuleReadModel> result = moduleFacade.getModulesByUserId(userId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(5)
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
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
    @DisplayName("createModule should throws AppUserNotFoundException when no user")
    void createModule_throws_AppUserNotFoundException() {
        // Given + When
        Throwable e = catchThrowable(() -> moduleFacade.createModule(1L, "foo"));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(AppUserNotFoundException.class)
        );
    }

    @Test
    @DisplayName("createModule should creates module with default name")
    void createModule_create_module_with_default_name() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        String moduleName = null;

        // + When
        ModuleReadModel result = moduleFacade.createModule(userId, moduleName);

        // Then
        assertAll(
                () -> assertThat(result.getModuleName()).isEqualTo("New module"),
                () -> assertThat(result.getSessionsPerDay()).isEqualTo(3),
                () -> assertThat(result.getPresentationsPerSession()).isEqualTo(5),
                () -> assertThat(result.getNewSentencesPerDay()).isEqualTo(1),
                () -> assertThat(result.getActualDay()).isEqualTo(1),
                () -> assertThat(result.getNextSession()).isEqualTo(1),
                () -> assertThat(result.isPrivate()).isTrue()
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }

    @Test
    @DisplayName("createModule should creates module with default specific name")
    void createModule_create_module_with_specific_name() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        String moduleName = "foo module";

        // + When
        ModuleReadModel result = moduleFacade.createModule(userId, moduleName);

        // Then
        assertAll(
                () -> assertThat(result.getModuleName()).isEqualTo(moduleName),
                () -> assertThat(result.getSessionsPerDay()).isEqualTo(3),
                () -> assertThat(result.getPresentationsPerSession()).isEqualTo(5),
                () -> assertThat(result.getNewSentencesPerDay()).isEqualTo(1),
                () -> assertThat(result.getActualDay()).isEqualTo(1),
                () -> assertThat(result.getNextSession()).isEqualTo(1),
                () -> assertThat(result.isPrivate()).isTrue()
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }

    @Test
    @DisplayName("createModule should throws IllegalArgumentException \"Invalid User ID.\"")
    void updateModule_throws_IllegalArgumentException_when_invalid_user_id() {
        // Given
        Long userId = -1L;
        ModuleWriteModel moduleToUpdate = new ModuleWriteModel();
        moduleToUpdate.setId(1L);

        //When
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
    @DisplayName("createModule should updates all fields")
    void updateModule_updates_all_fields() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        String moduleName = "foo module";
        ModuleReadModel userModule = moduleFacade.createModule(userId, moduleName);

        ModuleWriteModel toUpdate = ModuleWriteModel.builder()
                .moduleName("bar module")
                .id(userModule.getId())
                .sessionsPerDay(11)
                .presentationsPerSession(12)
                .newSentencesPerDay(13)
                .actualDay(14)
                .nextSession(15)
                .isPrivate(false)
                .build();

        // When
        ModuleReadModel result = moduleFacade.updateModule(userId, toUpdate);

        // Then
        assertAll(
                () -> assertThat(result.getModuleName()).isEqualTo(toUpdate.getModuleName()),
                () -> assertThat(result.getSessionsPerDay()).isEqualTo(toUpdate.getSessionsPerDay()),
                () -> assertThat(result.getPresentationsPerSession()).isEqualTo(toUpdate.getPresentationsPerSession()),
                () -> assertThat(result.getNewSentencesPerDay()).isEqualTo(toUpdate.getNewSentencesPerDay()),
                () -> assertThat(result.getActualDay()).isEqualTo(toUpdate.getActualDay()),
                () -> assertThat(result.getNextSession()).isEqualTo(toUpdate.getNextSession()),
                () -> assertThat(result.isPrivate()).isFalse()
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }

    @Test
    @DisplayName("createModule should updates all fields when given userId in ModuleWriteModel is different than userId")
    void updateModule_updates_all_fields_when_wrong_userId_in_write_model() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        String moduleName = "foo module";
        ModuleReadModel userModule = moduleFacade.createModule(userId, moduleName);

        ModuleWriteModel toUpdate = ModuleWriteModel.builder()
                .id(userModule.getId())
                .userId(userId + 16)
                .moduleName("bar module")
                .sessionsPerDay(11)
                .presentationsPerSession(12)
                .newSentencesPerDay(13)
                .actualDay(14)
                .nextSession(15)
                .isPrivate(false)
                .build();

        // When
        ModuleReadModel result = moduleFacade.updateModule(userId, toUpdate);

        // Then
        assertAll(
                () -> assertThat(result.getModuleName()).isEqualTo(toUpdate.getModuleName()),
                () -> assertThat(result.getSessionsPerDay()).isEqualTo(toUpdate.getSessionsPerDay()),
                () -> assertThat(result.getPresentationsPerSession()).isEqualTo(toUpdate.getPresentationsPerSession()),
                () -> assertThat(result.getNewSentencesPerDay()).isEqualTo(toUpdate.getNewSentencesPerDay()),
                () -> assertThat(result.getActualDay()).isEqualTo(toUpdate.getActualDay()),
                () -> assertThat(result.getNextSession()).isEqualTo(toUpdate.getNextSession()),
                () -> assertThat(result.isPrivate()).isFalse()
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }

    @Test
    @DisplayName("createModule should updates chosen fields")
    void updateModule_updates_chosen_fields() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        String moduleName = "foo module";
        ModuleReadModel userModule = moduleFacade.createModule(userId, moduleName);

        ModuleWriteModel toUpdate = ModuleWriteModel.builder()
                .id(userModule.getId())
                .sessionsPerDay(11)
                .actualDay(14)
                .nextSession(15)
                .build();

        // When
        ModuleReadModel result = moduleFacade.updateModule(userId, toUpdate);

        // Then
        assertAll(
                () -> assertThat(result.getModuleName()).isEqualTo(moduleName),
                () -> assertThat(result.getSessionsPerDay()).isEqualTo(toUpdate.getSessionsPerDay()),
                () -> assertThat(result.getPresentationsPerSession()).isEqualTo(userModule.getPresentationsPerSession()),
                () -> assertThat(result.getNewSentencesPerDay()).isEqualTo(userModule.getNewSentencesPerDay()),
                () -> assertThat(result.getActualDay()).isEqualTo(toUpdate.getActualDay()),
                () -> assertThat(result.getNextSession()).isEqualTo(toUpdate.getNextSession()),
                () -> assertThat(result.isPrivate()).isTrue()
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }

    @Test
    @DisplayName("createModule should do nothing when all argument fields are null")
    void updateModule_do_nothing() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        String moduleName = "foo module";
        ModuleReadModel userModule = moduleFacade.createModule(userId, moduleName);

        ModuleWriteModel toUpdate = ModuleWriteModel.builder()
                .id(userModule.getId())
                .build();

        // When
        ModuleReadModel result = moduleFacade.updateModule(userId, toUpdate);

        // Then
        assertAll(
                () -> assertThat(result.getModuleName()).isEqualTo(moduleName),
                () -> assertThat(result.getSessionsPerDay()).isEqualTo(userModule.getSessionsPerDay()),
                () -> assertThat(result.getPresentationsPerSession()).isEqualTo(userModule.getPresentationsPerSession()),
                () -> assertThat(result.getNewSentencesPerDay()).isEqualTo(userModule.getNewSentencesPerDay()),
                () -> assertThat(result.getActualDay()).isEqualTo(userModule.getActualDay()),
                () -> assertThat(result.getNextSession()).isEqualTo(userModule.getNextSession()),
                () -> assertThat(result.isPrivate()).isTrue()
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
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

    @Test
    @DisplayName("setNextSession should increment next session when nextSession is less then sessionsPerDay")
    void setNextSession_increments_next_session() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel userModule = moduleFacade.createModule(userId, null);
        Long moduleId = userModule.getId();

        // When
        moduleFacade.setNextSession(userId, moduleId);

        // Then
        ModuleReadModel updatedSession = moduleFacade.getModuleByUserIdAndModuleId(userId, moduleId);

        assertAll(
                () -> assertThat(updatedSession.getActualDay()).isEqualTo(userModule.getActualDay()),
                () -> assertThat(updatedSession.getNextSession()).isEqualTo(userModule.getNextSession() + 1)
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }

    @Test
    @DisplayName("setNextSession should increment actualDay and set nextSession to one when nextSession equal sessionsPerDay")
    void setNextSession_increments_actual_day_and_set_next_session() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel userModule = moduleFacade.createModule(userId, null);
        Long moduleId = userModule.getId();

        ModuleWriteModel toUpdate = ModuleWriteModel.builder()
                .id(userModule.getId())
                .nextSession(userModule.getSessionsPerDay())
                .build();
        moduleFacade.updateModule(userId, toUpdate);

        // When
        moduleFacade.setNextSession(userId, moduleId);

        // Then
        ModuleReadModel updatedSession = moduleFacade.getModuleByUserIdAndModuleId(userId, moduleId);

        assertAll(
                () -> assertThat(updatedSession.getActualDay()).isEqualTo(userModule.getActualDay() + 1),
                () -> assertThat(updatedSession.getNextSession()).isEqualTo(1)
        );

        // Clear Test Environment
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }




    /*----------------------*/




    /*-----------------------------*/


    private AppUserReadModel createTestUser() throws AppUserNotFoundException {
        AppUserWriteModel newUser = AppUserWriteModel.builder()
                .email("foo@email.com")
                .password("fooPassword")
                .build();

        return appUserFacade.create(newUser);
    }

    private void deleteTestUser(Long userId) throws Exception {
        AppUserReadModel user = appUserFacade.findUserById(userId);
        LoginTokenDto deleteToken = accountDeleteFacade.deleteUser(user);
        accountDeleteFacade.confirmDeleteToken(deleteToken.token());
    }

    private void createTestModules(Long userId, int modulesQuantity) throws AppUserNotFoundException {
        for (int i = 0; i < modulesQuantity; i++) {
            String moduleName = "testModule_" + (i + 1);
            moduleFacade.createModule(userId, moduleName);
            log.info("Created module: {}, for user: {}", moduleName, userId);
        }
    }

    private void deleteAllUserModules(Long userId) {
        List<ModuleReadModel> userModules = moduleFacade.getModulesByUserId(userId);
        for (ModuleReadModel module : userModules) {
            moduleFacade.deleteModule(module.getId(), userId);
        }
    }
}