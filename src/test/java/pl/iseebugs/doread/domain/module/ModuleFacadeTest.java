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

import javax.annotation.meta.When;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ModuleFacadeTest extends BaseIT {

    @Autowired
    ModuleFacade moduleFacade;

    @Autowired
    AppUserFacade appUserFacade;

    @Autowired
    AccountDeleteFacade accountDeleteFacade;
    
    @Autowired
    ModuleTestHelper moduleTestHelper;

    @Test
    @DisplayName("getModuleByUserIdAndModuleId should returns data")
    void getModuleByUserIdAndModuleId_returns_ModuleReadModel() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
        Long userId = savedUser.id();

        String moduleName = "fooModule";
        ModuleReadModel savedModule = moduleFacade.createModule(userId, moduleName);

        // When
        ModuleReadModel result = moduleFacade.getModuleByUserIdAndModuleId(userId, savedModule.getId());

        // Then
        assertAll(
                () -> assertThat(result.getModuleName()).isEqualTo(moduleName)
        );

        // Clear Test Environment
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getModulesByUserId should returns empty list when argument is non-exist user id")
    void getModulesByUserId_returns_Empty_List_when_no_user() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
        Long userId = savedUser.id();
        moduleTestHelper.createTestModules(userId, 10);

        Long nonExistUserId = 10000000L;

        // When
        List<ModuleReadModel> result = moduleFacade.getModulesByUserId(nonExistUserId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getModulesByUserId should returns empty list when no modules in database")
    void getModulesByUserId_returns_Empty_List_when_no_modules() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
        Long userId = savedUser.id();

        Long moduleId = 1L;

        // When
        List<ModuleReadModel> result = moduleFacade.getModulesByUserId(moduleId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getModulesByUserId should returns empty list when module id is deletedModuleId")
    void getModulesByUserId_returns_Empty_List_when_module_was_deleted() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel savedModule = moduleFacade.createModule(userId, "foo");
        Long moduleId = savedModule.getId();
        moduleTestHelper.deleteAllUserModules(userId);

        // When
        List<ModuleReadModel> result = moduleFacade.getModulesByUserId(moduleId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getModulesByUserId should returns list of modules")
    void getModulesByUserId_returns_List_of_modules() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
        Long userId = savedUser.id();

        moduleTestHelper.createTestModules(userId, 5);

        // When
        List<ModuleReadModel> result = moduleFacade.getModulesByUserId(userId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(5),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        moduleTestHelper.clearUserData(userId);
    }
    
    @Test
    @DisplayName("getAllModules should returns empty list")
    void getAllModules_return_Empty_List(){
        // When
        List<ModuleReadModel> result = moduleFacade.getAllModules();

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );
    }

    @Test
    @DisplayName("getAllModules should returns list with data")
    void getAllModules_returns_All_modules() throws Exception {
        // Given
        AppUserReadModel savedUser_1 = moduleTestHelper.createTestUser();
        Long userId_1 = savedUser_1.id();
        moduleTestHelper.createTestModules(userId_1, 5);

        AppUserReadModel savedUser_2 = moduleTestHelper.createTestUser("foo@mail.com");
        Long userId_2 = savedUser_2.id();
        moduleTestHelper.createTestModules(userId_2, 10);

        AppUserReadModel savedUser_3 = moduleTestHelper.createTestUser("bat@mail.com");
        Long userId_3 = savedUser_3.id();
        moduleTestHelper.createTestModules(userId_3, 3);
        
        // When
        List<ModuleReadModel> result = moduleFacade.getAllModules();
        
        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(18),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        moduleTestHelper.clearUserData(userId_1);
        moduleTestHelper.clearUserData(userId_2);
        moduleTestHelper.clearUserData(userId_3);
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
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
        Long userId = savedUser.id();

        String moduleName = null;

        // When
        ModuleReadModel result = moduleFacade.createModule(userId, moduleName);

        // Then
        assertAll(
                () -> assertThat(result.getModuleName()).isEqualTo("New module"),
                () -> assertThat(result.getSessionsPerDay()).isEqualTo(3),
                () -> assertThat(result.getPresentationsPerSession()).isEqualTo(8),
                () -> assertThat(result.getNewSentencesPerDay()).isEqualTo(1),
                () -> assertThat(result.getActualDay()).isEqualTo(1),
                () -> assertThat(result.getNextSession()).isEqualTo(1),
                () -> assertThat(result.isPrivate()).isTrue()
        );

        // Clear Test Environment
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("createModule should creates module with default specific name")
    void createModule_create_module_with_specific_name() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
        Long userId = savedUser.id();

        String moduleName = "foo module";

        // When
        ModuleReadModel result = moduleFacade.createModule(userId, moduleName);

        // Then
        assertAll(
                () -> assertThat(result.getModuleName()).isEqualTo(moduleName),
                () -> assertThat(result.getSessionsPerDay()).isEqualTo(3),
                () -> assertThat(result.getPresentationsPerSession()).isEqualTo(8),
                () -> assertThat(result.getNewSentencesPerDay()).isEqualTo(1),
                () -> assertThat(result.getActualDay()).isEqualTo(1),
                () -> assertThat(result.getNextSession()).isEqualTo(1),
                () -> assertThat(result.isPrivate()).isTrue()
        );

        // Clear Test Environment
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("createModule should updates all fields")
    void updateModule_updates_all_fields() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
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
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("createModule should updates all fields when given userId in ModuleWriteModel is different than userId")
    void updateModule_updates_all_fields_when_wrong_userId_in_write_model() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
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
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("createModule should updates chosen fields")
    void updateModule_updates_chosen_fields() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
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
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("createModule should do nothing when all argument fields are null")
    void updateModule_do_nothing() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
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
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("setNextSession should increment next session when nextSession is less then sessionsPerDay")
    void setNextSession_increments_next_session() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
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
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("setNextSession should increment actualDay and set nextSession to one when nextSession equal sessionsPerDay")
    void setNextSession_increments_actual_day_and_set_next_session() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
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
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("deleteModule should do nothing when no user id in db")
    void deleteModule_do_nothing_when_no_user_in_db() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel userModule = moduleFacade.createModule(userId, null);
        Long moduleId = userModule.getId();

        int dataSizeBefore = moduleFacade.getAllModules().size();

        // When
        moduleFacade.deleteModule(moduleId, userId + 1L);

        // Then
        int dataSizeAfter = moduleFacade.getAllModules().size();

        assertAll(
                () -> assertThat(dataSizeBefore).isEqualTo(dataSizeAfter)
        );

        // Clear Test Environment
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("deleteModule should do nothing when no module id in db")
    void deleteModule_do_nothing_when_no_module_in_db() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel userModule = moduleFacade.createModule(userId, null);
        Long moduleId = userModule.getId();

        int dataSizeBefore = moduleFacade.getAllModules().size();

        // When
        moduleFacade.deleteModule(moduleId + 1, userId);

        // Then
        int dataSizeAfter = moduleFacade.getAllModules().size();

        assertAll(
                () -> assertThat(dataSizeBefore).isEqualTo(dataSizeAfter)
        );

        // Clear Test Environment
        moduleTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("deleteModule should do nothing when no data")
    void deleteModule_deleted_when_not_found_data() throws Exception {
        // Given
        AppUserReadModel savedUser = moduleTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel userModule = moduleFacade.createModule(userId, null);
        Long moduleId = userModule.getId();

        int dataSizeBefore = moduleFacade.getAllModules().size();
        // When
        moduleFacade.deleteModule(moduleId, userId);

        // Then
        int dataSizeAfter = moduleFacade.getAllModules().size();

        assertAll(
                () -> assertThat(dataSizeBefore).isEqualTo(dataSizeAfter + 1)
        );

        // Clear Test Environment
        moduleTestHelper.clearUserData(userId);
    }
}