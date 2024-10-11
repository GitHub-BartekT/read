package pl.iseebugs.doread.domain.module;

import org.springframework.stereotype.Component;

@Component
class ModuleValidator {

    void longValidator(final Long id, String exceptionMessage) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }

    boolean longValidator(final Long id) {
        return id != null && id >= 1;
    }

    boolean integerValidator(final Integer id) {
        return id != null && id >= 1;
    }

    boolean stringValidator(String argument) {
        return argument != null && !argument.isBlank();
    }
}
