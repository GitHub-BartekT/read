package pl.iseebugs.doread.domain.sentence;

import org.springframework.stereotype.Component;

@Component
class SentenceValidator {

    void longValidator(final Long id, String exceptionMessage) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }

    boolean longValidator(final Long number) {
        return number != null && number >= 1;
    }

    boolean integerValidator(final Integer id) {
        return id != null && id >= 1;
    }

    boolean validateRange(Long smallerNumber, Long higherNumber){
       if(!longValidator(smallerNumber)){
           smallerNumber = 0L;
       }
        longValidator(higherNumber, "Invalid end of range number.");
        if(higherNumber < smallerNumber){
            throw new IllegalArgumentException("Beginning of range number is higher then end of range number.");
        }
        return true;
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
