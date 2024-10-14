package pl.iseebugs.doread.domain.sentence;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.BaseIT;
import pl.iseebugs.doread.domain.account.delete.AccountDeleteFacade;
import pl.iseebugs.doread.domain.module.ModuleFacade;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.sentence.dto.SentenceReadModel;
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
class SentenceFacadeTest extends BaseIT {

    @Autowired
    SentenceFacade sentenceFacade;

    @Autowired
    AppUserFacade appUserFacade;

    @Autowired
    ModuleFacade moduleFacade;

    @Autowired
    AccountDeleteFacade accountDeleteFacade;

    @Test
    @DisplayName("getAllByUserIdAndModuleId should throws IllegalArgumentException \"Invalid user id.\"")
    void getAllByUserIdAndModuleId_throws_IllegalArgumentException_when_invalid_userId(){
        // Given + When
        Throwable e = catchThrowable(() -> sentenceFacade.getAllByUserIdAndModuleId(-1L, 1L));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid user id.")
        );
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should throws IllegalArgumentException \"Invalid module id.\"")
    void getAllByUserIdAndModuleId_throws_IllegalArgumentException_when_invalid_moduleId(){
        // Given + When
        Throwable e = catchThrowable(() -> sentenceFacade.getAllByUserIdAndModuleId(1L, -1L));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid module id.")
        );
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns empty list when argument is non-exist user id")
    void getAllByUserIdAndModuleId_returns_Empty_List_when_no_user() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        createTestSentences(userId, module.getId(), 10);

        Long nonExistUserId = userId + 1;

        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(nonExistUserId, module.getId());

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns empty list when no modules in database")
    void getAllByUserIdAndModuleId_returns_Empty_List_when_no_modules() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        createTestSentences(userId, module.getId(), 10);

        Long nonExistModuleId = module.getId() + 1;

        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(userId, nonExistModuleId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns empty list when module id is deletedModuleId")
    void getAllByUserIdAndModuleId_returns_Empty_List_when_module_was_deleted() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        createTestSentences(userId, module.getId(), 10);
        Long moduleId = module.getId();

        deleteAllUserModulesAndSentences(moduleId);

        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(userId, moduleId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns list of ModuleReadModel")
    void getAllByUserIdAndModuleId_returns_List_of_ModuleReadModel() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        createTestSentences(userId, module.getId(), 10);
        Long moduleId = module.getId();

        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(userId, moduleId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(10),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns list of ModuleReadModel when in database are more sentences then one user's")
    void getAllByUserIdAndModuleId_returns_List_of_ModuleReadModel_from_more_than_one_user_data() throws Exception {
        // Given
        AppUserReadModel savedUser_1 = createTestUser("foo@mail.com");
        Long userId_1 = savedUser_1.id();
        ModuleReadModel module_1 = moduleFacade.createModule(userId_1, null);
        createTestSentences(userId_1, module_1.getId(), 10);
        Long moduleId_1 = module_1.getId();

        AppUserReadModel savedUser_2 = createTestUser("bar@mail.com");
        Long userId_2 = savedUser_2.id();
        ModuleReadModel module_2 = moduleFacade.createModule(userId_2, null);
        createTestSentences(userId_2, module_2.getId(), 10);
        Long moduleId_2 = module_2.getId();

        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(userId_2, moduleId_2);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(10),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        clearUserData(userId_1);
        clearUserData(userId_2);
    }



    /*---------------------------*/


    @Test
    void findAllSentenceByModuleId() {
    }



    /*---------------------------*/


    /*---------------------------*/


    @Test
    void findAllByModuleIdAndBetween() {
    }



    /*---------------------------*/


    /*---------------------------*/


    @Test
    void findById() {
    }


    /*---------------------------*/


    private AppUserReadModel createTestUser(String email) throws AppUserNotFoundException {
        AppUserWriteModel newUser = AppUserWriteModel.builder()
                .email(email)
                .password("fooPassword")
                .build();

        return appUserFacade.create(newUser);
    }

    private AppUserReadModel createTestUser() throws AppUserNotFoundException {
        return createTestUser("some.foo.bar@email.com");
    }

    private void deleteTestUser(Long userId) throws Exception {
        AppUserReadModel user = appUserFacade.findUserById(userId);
        appUserFacade.anonymization(user.id());
    }

    private void createTestSentences(Long userId, Long moduleId, int modulesQuantity) throws AppUserNotFoundException {
        for (int i = 0; i < modulesQuantity; i++) {
            String sentenceText = "testSentence_" + (i + 1);
            sentenceFacade.create(userId, moduleId, sentenceText);
            log.info("Created module: {}, for user: {}", sentenceText, userId);
        }
    }

    private void createTestModules(Long userId, int modulesQuantity) throws AppUserNotFoundException {
        for (int i = 0; i < modulesQuantity; i++) {
            String moduleName = "testModule_" + (i + 1);
            moduleFacade.createModule(userId, moduleName);
            log.info("Created module: {}, for user: {}", moduleName, userId);
        }
    }

    private void deleteAllSentencesInModule(Long userId, Long moduleId) {
        List<SentenceReadModel> userSentences = sentenceFacade.getAllByUserIdAndModuleId(userId, moduleId);
        for (SentenceReadModel sentence : userSentences) {
            sentenceFacade.deleteById(sentence.getId());
        }
    }

    private void deleteAllUserModulesAndSentences(Long userId) {
        List<ModuleReadModel> userModules = moduleFacade.getModulesByUserId(userId);
        for (ModuleReadModel module : userModules) {
            deleteAllSentencesInModule(userId, module.getId());
            moduleFacade.deleteModule(module.getId(), userId);
        }
    }

    private void clearUserData(final Long userId) throws Exception {
        deleteAllUserModulesAndSentences(userId);
        deleteTestUser(userId);
    }
}