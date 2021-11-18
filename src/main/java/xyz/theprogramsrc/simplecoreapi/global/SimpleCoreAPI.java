package xyz.theprogramsrc.simplecoreapi.global;

public class SimpleCoreAPI {

    private static SimpleCoreAPI instance;
    private final ModuleManager moduleManager;

    public SimpleCoreAPI() {
        instance = this;
        this.moduleManager = ModuleManager.init();
    }

    public static SimpleCoreAPI getInstance() {
        return instance;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

}
