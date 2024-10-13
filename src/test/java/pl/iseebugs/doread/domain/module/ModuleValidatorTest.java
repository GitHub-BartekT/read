package pl.iseebugs.doread.domain.module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

class ModuleValidatorTest {

    private ModuleValidator moduleValidator;

    @BeforeEach
    void setUp() {
        moduleValidator = new ModuleValidator();
    }

    @Test
    @DisplayName("longValidator should throws IllegalArgumentException when argument is null")
    void longValidator_throws_IllegalArgumentException_when_argument_is_null() {
        //Given
        Long argument = null;
        String message = "bar";

        //When
        Throwable e = catchThrowable(() -> moduleValidator.longValidator(argument, message));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo(message)
        );
    }

    @Test
    @DisplayName("longValidator should throws IllegalArgumentException when argument is less then 1")
    void longValidator_throws_IllegalArgumentException_when_argument_is_less_then_1() {
        //Given
        Long argument = 0L;
        String message = "foo";

        //When
        Throwable e = catchThrowable(() -> moduleValidator.longValidator(argument, message));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(e.getMessage()).isEqualTo(message)
        );
    }

    @Test
    @DisplayName("longValidator should does nothing after validation")
    void longValidator_does_nothing() {
        //Given
        Long argument = 2L;
        String message = "foo";

        //When
        moduleValidator.longValidator(argument, message);

        // Then
        assertDoesNotThrow(() -> moduleValidator.longValidator(argument, message));
    }

    @Test
    void integerValidator() {
    }

    @Test
    void stringValidator() {
    }

    @Test
    void validateAndSetDefaultModuleName() {
    }
}