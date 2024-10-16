package pl.iseebugs.doread.domain.sentence;

import lombok.AllArgsConstructor;
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
import pl.iseebugs.doread.domain.sentence.dto.SentenceWriteModel;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

import java.util.ArrayList;
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
    SentenceTestHelper sentenceTestHelper;

    @Autowired
    ModuleFacade moduleFacade;

    @Autowired
    AccountDeleteFacade accountDeleteFacade;

    @Test
    @DisplayName("getAllByUserIdAndModuleId should throws IllegalArgumentException \"Invalid user id.\"")
    void getAllByUserIdAndModuleId_throws_IllegalArgumentException_when_invalid_userId() {
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
    void getAllByUserIdAndModuleId_throws_IllegalArgumentException_when_invalid_moduleId() {
        // Given + When
        Throwable e = catchThrowable(() -> sentenceFacade.getAllByUserIdAndModuleId(1L, -1L));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid module id.")
        );
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should return empty list when no sentences")
    void getAllByUserIdAndModuleId_returns_Empty_List_when_no_sentences() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");

        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(userId, module.getId());

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.size()).isEqualTo(0)
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns empty list when argument is non-exist user id")
    void getAllByUserIdAndModuleId_returns_Empty_List_when_no_user() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);

        Long nonExistUserId = userId + 1;

        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(nonExistUserId, module.getId());

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns empty list when no modules in database")
    void getAllByUserIdAndModuleId_returns_Empty_List_when_no_modules() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);

        Long nonExistModuleId = module.getId() + 1;

        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(userId, nonExistModuleId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns empty list when module id is deletedModuleId")
    void getAllByUserIdAndModuleId_returns_Empty_List_when_module_was_deleted() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);
        Long moduleId = module.getId();

        sentenceTestHelper.deleteAllUserModulesAndSentences(userId);

        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(userId, moduleId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns list of ModuleReadModel")
    void getAllByUserIdAndModuleId_returns_List_of_ModuleReadModel() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);
        Long moduleId = module.getId();

        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(userId, moduleId);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(10),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns list of ModuleReadModel when in database are more sentences then one user's")
    void getAllByUserIdAndModuleId_returns_List_of_ModuleReadModel_from_more_than_one_user_data() throws Exception {
        // Given
        AppUserReadModel savedUser_1 = sentenceTestHelper.createTestUser("foo@mail.com");
        Long userId_1 = savedUser_1.id();
        ModuleReadModel module_1 = moduleFacade.createModule(userId_1, null);
        sentenceTestHelper.createTestSentences(userId_1, module_1.getId(), 10);
        Long moduleId_1 = module_1.getId();

        AppUserReadModel savedUser_2 = sentenceTestHelper.createTestUser("bar@mail.com");
        Long userId_2 = savedUser_2.id();
        ModuleReadModel module_2 = moduleFacade.createModule(userId_2, null);
        sentenceTestHelper.createTestSentences(userId_2, module_2.getId(), 10);
        Long moduleId_2 = module_2.getId();

        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(userId_2, moduleId_2);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(10),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId_1);
        sentenceTestHelper.clearUserData(userId_2);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should return sentences sorted by ordinalNumber")
    void getAllByUserIdAndModuleId_returns_Sentences_Sorted_By_OrdinalNumber() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        sentenceTestHelper.createTestSentences(userId, module.getId(), 5);
        // When
        List<SentenceReadModel> result = sentenceFacade.getAllByUserIdAndModuleId(userId, module.getId());

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.size()).isEqualTo(5),
                () -> assertThat(result.get(0).getOrdinalNumber()).isEqualTo(1L),
                () -> assertThat(result.get(1).getOrdinalNumber()).isEqualTo(2L),
                () -> assertThat(result.get(2).getOrdinalNumber()).isEqualTo(3L),
                () -> assertThat(result.get(3).getOrdinalNumber()).isEqualTo(4L),
                () -> assertThat(result.get(4).getOrdinalNumber()).isEqualTo(5L)
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getAllSentenceByModuleId should return sentences as strings")
    void getAllSentenceByModuleId_returns_List_of_Sentence_Strings() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        sentenceTestHelper.createTestSentences(userId, module.getId(), 3);

        // When
        List<String> result = sentenceFacade.getAllSentenceByModuleId(userId, module.getId());

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.size()).isEqualTo(3)
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("findAllByModuleIdAndBetween should return empty list when no sentences")
    void findAllByModuleIdAndBetween_Empty_List_when_no_sentences() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");

        Long startOfRange = 2L;
        Long endOfRange = 5L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(userId, module.getId(), startOfRange, endOfRange);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.size()).isEqualTo(0)
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("findAllByModuleIdAndBetween should returns empty list when argument is non-exist user id")
    void findAllByModuleIdAndBetween_returns_Empty_List_when_no_user() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);

        Long nonExistUserId = userId + 1;

        Long startOfRange = 2L;
        Long endOfRange = 5L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(nonExistUserId, module.getId(), startOfRange, endOfRange);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("findAllByModuleIdAndBetween should returns empty list when no modules in database")
    void findAllByModuleIdAndBetween_returns_Empty_List_when_no_modules() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);

        Long nonExistModuleId = module.getId() + 1;

        Long startOfRange = 2L;
        Long endOfRange = 5L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(userId, nonExistModuleId, startOfRange, endOfRange);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("findAllByModuleIdAndBetween should returns empty list when module id is deletedModuleId")
    void findAllByModuleIdAndBetween_returns_Empty_List_when_module_was_deleted() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);
        Long moduleId = module.getId();

        sentenceTestHelper.deleteAllUserModulesAndSentences(userId);

        Long startOfRange = 2L;
        Long endOfRange = 5L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(userId, moduleId, startOfRange, endOfRange);

        System.out.println("Module name: " + module.getModuleName());
        for (SentenceReadModel sentence : result
        ) {
            System.out.printf("Sentence %s, ordinal number: %d \n ", sentence.getSentence(), sentence.getOrdinalNumber());
        }

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns list of ModuleReadModel when the range starts at beginning of sentence ordinal number and end in the middle of")
    void findAllByModuleIdAndBetween_returns_List_of_ModuleReadModel_from_beginning_of_data() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);
        Long moduleId = module.getId();

        Long startOfRange = 1L;
        Long endOfRange = 5L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(userId, moduleId, startOfRange, endOfRange);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(5),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns list of ModuleReadModel when the range starts in the middle of the range and end at the end of it")
    void findAllByModuleIdAndBetween_returns_List_of_ModuleReadModel_from_the_end_of_data() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);
        Long moduleId = module.getId();

        Long startOfRange = 9L;
        Long endOfRange = 10L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(userId, moduleId, startOfRange, endOfRange);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns list of ModuleReadModel when the range starts in the middle of the range and end at before the end")
    void findAllByModuleIdAndBetween_returns_List_of_ModuleReadModel_from_the_middle_of_data() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);
        Long moduleId = module.getId();

        Long startOfRange = 4L;
        Long endOfRange = 7L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(userId, moduleId, startOfRange, endOfRange);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(4),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns list when beginning of range is less than 1")
    void findAllByModuleIdAndBetween_returns_empty_list_when_start_range_number_is_less_than_1() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);
        Long moduleId = module.getId();

        Long startOfRange = -5L;
        Long endOfRange = 5L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(userId, moduleId, startOfRange, endOfRange);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(5),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("getAllByUserIdAndModuleId should returns list when the end range number is out of range")
    void findAllByModuleIdAndBetween_returns_empty_list_when_end_range_number_is_out_of_range() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);
        Long moduleId = module.getId();

        Long startOfRange = 9L;
        Long endOfRange = 15L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(userId, moduleId, startOfRange, endOfRange);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("findAllByModuleIdAndBetween should returns empty list when range is out of range")
    void findAllByModuleIdAndBetween_returns_empty_list_when_range_is_out_of_range() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, null);
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);
        Long moduleId = module.getId();

        Long startOfRange = 20L;
        Long endOfRange = 30L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(userId, moduleId, startOfRange, endOfRange);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("findAllByModuleIdAndBetween should returns list of ModuleReadModel when in database are more sentences then one user's")
    void findAllByModuleIdAndBetween_returns_List_of_ModuleReadModel_from_more_than_one_user_data() throws Exception {
        // Given
        AppUserReadModel savedUser_1 = sentenceTestHelper.createTestUser("foo@mail.com");
        Long userId_1 = savedUser_1.id();
        ModuleReadModel module_1 = moduleFacade.createModule(userId_1, null);
        sentenceTestHelper.createTestSentences(userId_1, module_1.getId(), 10);

        AppUserReadModel savedUser_2 = sentenceTestHelper.createTestUser("bar@mail.com");
        Long userId_2 = savedUser_2.id();
        ModuleReadModel module_2 = moduleFacade.createModule(userId_2, null);
        sentenceTestHelper.createTestSentences(userId_2, module_2.getId(), 10);
        Long moduleId_2 = module_2.getId();

        Long startOfRange = 6L;
        Long endOfRange = 10L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(userId_2, moduleId_2, startOfRange, endOfRange);

        // Then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(5),
                () -> assertThat(result).isNotNull()
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId_1);
        sentenceTestHelper.clearUserData(userId_2);
    }

    @Test
    @DisplayName("findAllByModuleIdAndBetween should return sentences sorted by ordinalNumber")
    void findAllByModuleIdAndBetween_returns_Sentences_Sorted_By_OrdinalNumber() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        sentenceTestHelper.createTestSentences(userId, module.getId(), 10);

        Long startOfRange = 6L;
        Long endOfRange = 10L;

        // When
        List<SentenceReadModel> result = sentenceFacade.findAllByModuleIdAndBetween(userId, module.getId(), startOfRange, endOfRange);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.size()).isEqualTo(5),
                () -> assertThat(result.get(0).getOrdinalNumber()).isEqualTo(6L),
                () -> assertThat(result.get(1).getOrdinalNumber()).isEqualTo(7L),
                () -> assertThat(result.get(2).getOrdinalNumber()).isEqualTo(8L),
                () -> assertThat(result.get(3).getOrdinalNumber()).isEqualTo(9L),
                () -> assertThat(result.get(4).getOrdinalNumber()).isEqualTo(10L)
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("findByUserIdAndId should throws IllegalArgumentException \"Invalid user id.\" when invalid user id")
    void findByUserIdAndId_throws_IllegalArgumentException_when_invalid_user_id() {
        // Given
        Long userId = -1L;
        Long sentenceId = 1L;

        // When
        Throwable e = catchThrowable(() -> sentenceFacade.findByUserIdAndId(userId, sentenceId));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid user id.")
        );
    }

    @Test
    @DisplayName("findByUserIdAndId should throws IllegalArgumentException \"Invalid sentence id.\" when invalid sentence id")
    void findByUserIdAndId_throws_IllegalArgumentException_when_invalid_sentence_id() {
        // Given
        Long userId = 1L;
        Long sentenceId = -1L;

        // When
        Throwable e = catchThrowable(() -> sentenceFacade.findByUserIdAndId(userId, sentenceId));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid sentence id.")
        );
    }

    @Test
    @DisplayName("findByUserIdAndId should throws SentenceNotFoundException")
    void findByUserIdAndId_throws_SentenceNotFoundException() {
        // Given + When
        Throwable e = catchThrowable(() -> sentenceFacade.findByUserIdAndId(1L, 1L));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(SentenceNotFoundException.class)
        );
    }

    @Test
    @DisplayName("findByUserIdAndId should returns data")
    void findByUserIdAndId_returns_data() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");

        String text = "foo sentence";
        SentenceReadModel toSave = sentenceFacade.create(userId, module.getId(), text);

        // When
        SentenceReadModel result = sentenceFacade.findByUserIdAndId(userId, toSave.getId());

        // Then
        assertAll(
                () -> assertThat(result.getUserId()).isEqualTo(userId),
                () -> assertThat(result.getSentence()).isEqualTo(text)
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("create should throws IllegalArgumentException \"Invalid user id.\"")
    void create_throws_IllegalArgumentException_when_invalid_userId() {
        // Given
        Long userId = -1L;
        Long moduleId = 1L;
        String sentenceText = "foo";

        // When
        Throwable e = catchThrowable(() -> sentenceFacade.create(userId, moduleId, sentenceText));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid user id.")
        );
    }

    @Test
    @DisplayName("create should throws IllegalArgumentException \"Invalid module id.\"")
    void create_throws_IllegalArgumentException_when_invalid_moduleId() {
        // Given
        Long userId = 1L;
        Long moduleId = -1L;
        String sentenceText = "foo";

        // When
        Throwable e = catchThrowable(() -> sentenceFacade.create(userId, moduleId, sentenceText));


        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid module id.")
        );
    }

    @Test
    @DisplayName("create should return new sentence with default text when text is null")
    void create_returns_sentence_with_default_text_when_text_is_null() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");

        String sentenceText = null;

        // When
        SentenceReadModel result = sentenceFacade.create(userId, module.getId(), sentenceText);

        // Then
        assertAll(
                () -> assertThat(result.getSentence()).isEqualTo("New module"),
                () -> assertThat(result.getUserId()).isEqualTo(userId),
                () -> assertThat(result.getModuleId()).isEqualTo(module.getId())
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("create should return new sentence with default text when text is blank")
    void create_returns_sentence_with_default_text_when_text_is_blank() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");

        String sentenceText = "";

        // When
        SentenceReadModel result = sentenceFacade.create(userId, module.getId(), sentenceText);

        // Then
        assertAll(
                () -> assertThat(result.getSentence()).isEqualTo("New module"),
                () -> assertThat(result.getUserId()).isEqualTo(userId),
                () -> assertThat(result.getModuleId()).isEqualTo(module.getId())
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("create should return new sentence with given text")
    void create_returns_sentence_with_given_text() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");

        String sentenceText = "foo text";

        // When
        SentenceReadModel result = sentenceFacade.create(userId, module.getId(), sentenceText);

        // Then
        assertAll(
                () -> assertThat(result.getSentence()).isEqualTo(sentenceText),
                () -> assertThat(result.getUserId()).isEqualTo(userId),
                () -> assertThat(result.getModuleId()).isEqualTo(module.getId())
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("create should return new sentence with ordinal number equals 1 when no sentences in module")
    void create_returns_sentence_with_ordinal_number_equals_one() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");

        String sentenceText = "foo text";

        // When
        SentenceReadModel result = sentenceFacade.create(userId, module.getId(), sentenceText);

        // Then
        assertAll(
                () -> assertThat(result.getOrdinalNumber()).isEqualTo(1L),
                () -> assertThat(result.getUserId()).isEqualTo(userId),
                () -> assertThat(result.getModuleId()).isEqualTo(module.getId())
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("create should return new sentence with ordinal number equals existing set of sentences plus one")
    void create_returns_sentence_with_ordinal_number_equals_five() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        sentenceTestHelper.createTestSentences(userId, module.getId(), 20);

        String sentenceText = "foo text";

        // When
        SentenceReadModel result = sentenceFacade.create(userId, module.getId(), sentenceText);

        // Then
        assertAll(
                () -> assertThat(result.getOrdinalNumber()).isEqualTo(21L),
                () -> assertThat(result.getUserId()).isEqualTo(userId),
                () -> assertThat(result.getModuleId()).isEqualTo(module.getId())
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }



    /*---------------------------*/



    @Test
    @DisplayName("deleteByUserIdAndModuleIdAndOrdinalNumber should throws IllegalArgumentException \"Invalid user id.\"")
    void deleteByUserIdAndModuleIdAndOrdinalNumber_throws_IllegalArgumentException_when_invalid_userId() {
        // Given
        Long userId = -1L;
        Long moduleId = 1L;
        Long ordinalNumber = 1L;

        // When
        Throwable e = catchThrowable(() -> sentenceFacade.deleteByUserIdAndModuleIdAndOrdinalNumber(userId, moduleId, ordinalNumber));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid user id.")
        );
    }

    @Test
    @DisplayName("deleteByUserIdAndModuleIdAndOrdinalNumber should throws IllegalArgumentException \"Invalid module id.\"")
    void deleteByUserIdAndModuleIdAndOrdinalNumber_throws_IllegalArgumentException_when_invalid_moduleId() {
        // Given
        Long userId = 1L;
        Long moduleId = -1L;
        Long ordinalNumber = 1L;

        // When
        Throwable e = catchThrowable(() -> sentenceFacade.deleteByUserIdAndModuleIdAndOrdinalNumber(userId, moduleId, ordinalNumber));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid module id.")
        );
    }

    @Test
    @DisplayName("deleteByUserIdAndModuleIdAndOrdinalNumber should throws IllegalArgumentException \"Invalid ordinalNumber\"")
    void deleteByUserIdAndModuleIdAndOrdinalNumber_throws_IllegalArgumentException_when_invalid_ordinalNumber() {
        // Given
        Long userId = 1L;
        Long moduleId = 1L;
        Long ordinalNumber = -1L;

        // When
        Throwable e = catchThrowable(() -> sentenceFacade.deleteByUserIdAndModuleIdAndOrdinalNumber(userId, moduleId, ordinalNumber));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo("Invalid ordinalNumber")
        );
    }

    @Test
    @DisplayName("deleteByUserIdAndModuleIdAndOrdinalNumber should return empty list when no user found")
    void deleteByUserIdAndModuleIdAndOrdinalNumber_returns_empty_list_when_no_user_found() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();
        Long noExistsUserId = userId + 1;

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        sentenceTestHelper.createTestSentences(userId, module.getId(), 5);

        // When
        List<SentenceReadModel> result = sentenceFacade
                .deleteByUserIdAndModuleIdAndOrdinalNumber(noExistsUserId, module.getId(), 2L);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.size()).isEqualTo(0)
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("deleteByUserIdAndModuleIdAndOrdinalNumber should return empty list when no module found")
    void deleteByUserIdAndModuleIdAndOrdinalNumber_returns_empty_list_when_no_module_found() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        Long noExistsModuleId = module.getId() + 1;
        sentenceTestHelper.createTestSentences(userId, module.getId(), 5);

        // When
        List<SentenceReadModel> result = sentenceFacade
                .deleteByUserIdAndModuleIdAndOrdinalNumber(userId, noExistsModuleId, 2L);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.size()).isEqualTo(0)
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("deleteByUserIdAndModuleIdAndOrdinalNumber should return empty list when the user isn't the owner of the module")
    void deleteByUserIdAndModuleIdAndOrdinalNumber_returns_empty_list_when_user_and_module_do_not_match_each_ather() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser("foo@mail.com");
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        Long noExistsModuleId = module.getId() + 1;
        sentenceTestHelper.createTestSentences(userId, module.getId(), 5);

        AppUserReadModel savedUser_2 = sentenceTestHelper.createTestUser("bar@mail.com");
        Long userId_2 = savedUser_2.id();

        // When
        List<SentenceReadModel> result = sentenceFacade
                .deleteByUserIdAndModuleIdAndOrdinalNumber(userId_2, noExistsModuleId, 2L);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.size()).isEqualTo(0)
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
        sentenceTestHelper.clearUserData(userId_2);
    }

    @Test
    @DisplayName("deleteByUserIdAndModuleIdAndOrdinalNumber should return empty list when no sentences in user's module")
    void deleteByUserIdAndModuleIdAndOrdinalNumber_returns_empty_list_when_user_module_has_no_sentences() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        Long moduleId = module.getId();

        // When
        List<SentenceReadModel> result = sentenceFacade
                .deleteByUserIdAndModuleIdAndOrdinalNumber(userId, moduleId, 1L);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.size()).isEqualTo(0)
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("deleteByUserIdAndModuleIdAndOrdinalNumber should do nothing when ordinal number is out of range," +
            " and return list in ascending ordinal by ordinalNumber")
    void deleteByUserIdAndModuleIdAndOrdinalNumber_do_nothing() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        Long moduleId = module.getId();

        sentenceTestHelper.createTestSentences(userId, moduleId, 3);

        List<SentenceReadModel> listBefore = sentenceFacade.getAllByUserIdAndModuleId(userId, moduleId);

        // When
        List<SentenceReadModel> result = sentenceFacade
                .deleteByUserIdAndModuleIdAndOrdinalNumber(userId, moduleId, 4L);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.size()).isEqualTo(3),
                () -> assertThat(listBefore.size()).isEqualTo(result.size()),
                () -> assertThat(result.get(0).getOrdinalNumber()).isEqualTo(1),
                () -> assertThat(result.get(1).getOrdinalNumber()).isEqualTo(2),
                () -> assertThat(result.get(2).getOrdinalNumber()).isEqualTo(3)
                );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("deleteByUserIdAndModuleIdAndOrdinalNumber should deletes first position," +
            " and return all module sentences in ascending ordinal by ordinalNumber starting from 1")
    void deleteByUserIdAndModuleIdAndOrdinalNumber_deleting_first_position() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        Long moduleId = module.getId();

        sentenceTestHelper.createTestSentences(userId, moduleId, 3);

        List<SentenceReadModel> listBefore = sentenceFacade.getAllByUserIdAndModuleId(userId, moduleId);

        // When
        List<SentenceReadModel> result = sentenceFacade
                .deleteByUserIdAndModuleIdAndOrdinalNumber(userId, moduleId, 1L);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(listBefore.size()).isEqualTo(result.size() + 1),
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result.get(0).getOrdinalNumber()).isEqualTo(1),
                () -> assertThat(result.get(0).getSentence()).isEqualTo("testSentence_2"),
                () -> assertThat(result.get(1).getOrdinalNumber()).isEqualTo(2),
                () -> assertThat(result.get(1).getSentence()).isEqualTo("testSentence_3")
                );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("deleteByUserIdAndModuleIdAndOrdinalNumber should deletes last position," +
            " and return all module sentences in ascending ordinal by ordinalNumber starting from 1")
    void deleteByUserIdAndModuleIdAndOrdinalNumber_deleting_last_position() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        Long moduleId = module.getId();

        sentenceTestHelper.createTestSentences(userId, moduleId, 3);

        List<SentenceReadModel> listBefore = sentenceFacade.getAllByUserIdAndModuleId(userId, moduleId);

        // When
        List<SentenceReadModel> result = sentenceFacade
                .deleteByUserIdAndModuleIdAndOrdinalNumber(userId, moduleId, 3L);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(listBefore.size()).isEqualTo(result.size() + 1),
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result.get(0).getOrdinalNumber()).isEqualTo(1),
                () -> assertThat(result.get(0).getSentence()).isEqualTo("testSentence_1"),
                () -> assertThat(result.get(1).getOrdinalNumber()).isEqualTo(2),
                () -> assertThat(result.get(1).getSentence()).isEqualTo("testSentence_2")
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }

    @Test
    @DisplayName("deleteByUserIdAndModuleIdAndOrdinalNumber should deletes mid position," +
            " and return all module sentences in ascending ordinal by ordinalNumber starting from 1")
    void deleteByUserIdAndModuleIdAndOrdinalNumber_deleting_mid_position() throws Exception {
        // Given
        AppUserReadModel savedUser = sentenceTestHelper.createTestUser();
        Long userId = savedUser.id();

        ModuleReadModel module = moduleFacade.createModule(userId, "testModule");
        Long moduleId = module.getId();

        sentenceTestHelper.createTestSentences(userId, moduleId, 3);

        List<SentenceReadModel> listBefore = sentenceFacade.getAllByUserIdAndModuleId(userId, moduleId);

        // When
        List<SentenceReadModel> result = sentenceFacade
                .deleteByUserIdAndModuleIdAndOrdinalNumber(userId, moduleId, 2L);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(listBefore.size()).isEqualTo(result.size() + 1),
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result.get(0).getOrdinalNumber()).isEqualTo(1),
                () -> assertThat(result.get(0).getSentence()).isEqualTo("testSentence_1"),
                () -> assertThat(result.get(1).getOrdinalNumber()).isEqualTo(2),
                () -> assertThat(result.get(1).getSentence()).isEqualTo("testSentence_3")
        );

        // Clear Test Environment
        sentenceTestHelper.clearUserData(userId);
    }
}