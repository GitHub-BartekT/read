package pl.iseebugs.doread.domain.sentence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

class SentenceValidationTest {

    private SentenceFacade sentenceFacade;

    @BeforeEach
    void setUp() {
        var mockSentenceRepository = mock(SentenceRepository.class);
        var mockSentenceProperties = mock(SentencesProperties.class);

        sentenceFacade = new SentenceFacade(
                mockSentenceRepository,
                mockSentenceProperties,
                new SentenceValidator()
        );
    }

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
}
