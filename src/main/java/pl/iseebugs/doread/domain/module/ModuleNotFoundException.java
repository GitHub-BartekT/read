package pl.iseebugs.doread.domain.module;

public class ModuleNotFoundException extends Exception{
    public ModuleNotFoundException() {
        super("Module not found.");
    }
    public ModuleNotFoundException(String message) {
        super(message);
    }
}
