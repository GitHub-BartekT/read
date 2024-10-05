package pl.iseebugs.doread.domain.module;

class ModuleValidator {

    void longArgumentValidator(final Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("Id is invalid.");
        }
    }

    boolean stringArgumentValidator(String argument) {
        return argument != null && !argument.isBlank();
    }
}
