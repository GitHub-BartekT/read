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
    @DisplayName("integerValidator should returns false when argument is null")
    void integerValidator_returns_false_when_argument_is_null() {
        //Given
        Integer argument = null;

        //When
        boolean result = moduleValidator.integerValidator(argument);

        // Then
        assertAll(
                () -> assertThat(result).isFalse()
        );
    }

    @Test
    @DisplayName("integerValidator should returns false when argument is negative")
    void integerValidator_returns_false_when_argument_is_equals_minus_one() {
        //Given
        Integer argument = -1;

        //When
        boolean result = moduleValidator.integerValidator(argument);

        // Then
        assertAll(
                () -> assertThat(result).isFalse()
        );
    }

    @Test
    @DisplayName("integerValidator should returns false when argument is zero")
    void integerValidator_returns_false_when_argument_is_equals_zero() {
        //Given
        Integer argument = 0;

        //When
        boolean result = moduleValidator.integerValidator(argument);

        // Then
        assertAll(
                () -> assertThat(result).isFalse()
        );
    }

    @Test
    @DisplayName("integerValidator should returns true when argument is zero")
    void integerValidator_returns_true_when_argument_is_equals_one() {
        //Given
        Integer argument = 1;

        //When
        boolean result = moduleValidator.integerValidator(argument);

        // Then
        assertAll(
                () -> assertThat(result).isTrue()
        );
    }

    @Test
    @DisplayName("integerValidator should returns true when argument is positive")
    void integerValidator_returns_true_when_argument_is_equals_five() {
        //Given
        Integer argument = 5;

        //When
        boolean result = moduleValidator.integerValidator(argument);

        // Then
        assertAll(
                () -> assertThat(result).isTrue()
        );
    }


    @Test
    @DisplayName("stringValidator should returns false when argument is null")
    void stringValidator_returns_false_when_argument_is_null() {
        //Given
        String argument = null;

        //When
        boolean result = moduleValidator.stringValidator(argument);

        // Then
        assertAll(
                () -> assertThat(result).isFalse()
        );
    }

    @Test
    @DisplayName("stringValidator should returns false when argument is blank")
    void stringValidator_returns_false_when_argument_is_blank() {
        //Given
        String argument = "";

        //When
        boolean result = moduleValidator.stringValidator(argument);

        // Then
        assertAll(
                () -> assertThat(result).isFalse()
        );
    }

    @Test
    @DisplayName("stringValidator should returns true when argument is valid")
    void stringValidator_returns_true() {
        //Given
        String argument = "foo";

        //When
        boolean result = moduleValidator.stringValidator(argument);

        // Then
        assertAll(
                () -> assertThat(result).isTrue()
        );
    }

    @Test
    @DisplayName("validateAndSetDefaultModuleName returns 'New module' when stringValidator returns false")
    void validateAndSetDefaultModuleName_returns_New_module_when_string_validator_returns_false() {
        //Given
        String invalidModuleName = "";

        //When
        String result = moduleValidator.validateAndSetDefaultModuleName(invalidModuleName);

        //Then
        assertAll(
                () -> assertThat(result).isEqualTo("New module")
        );
    }

    @Test
    @DisplayName("validateAndSetDefaultModuleName returns original name when stringValidator returns true")
    void validateAndSetDefaultModuleName_returns_original_name_when_string_validator_returns_true() {
        //Given
        String validModuleName = "Valid Module Name";

        //When
        String result = moduleValidator.validateAndSetDefaultModuleName(validModuleName);

        //Then
        assertAll(
                () -> assertThat(result).isEqualTo(validModuleName)
        );
    }
}