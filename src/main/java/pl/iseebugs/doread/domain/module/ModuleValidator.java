package pl.iseebugs.doread.domain.module;

import org.springframework.stereotype.Component;

@Component
class ModuleValidator {

    void longValidator(final Long id, String exceptionMessage) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }

    boolean integerValidator(final Integer id) {
        return id != null && id >= 1;
    }

    boolean stringValidator(String argument) {
        return argument != null && !argument.isBlank();
    }

    String validateAndSetDefaultModuleName(String moduleName) {
        if (!stringValidator(moduleName)) {
            moduleName = "New module";
        }
        return moduleName;
    }
}
