package xyz.theprogramsrc.simplecoreapi.global.object;

public class ModuleDescription {

    private final String mainClass;
    private final String name;
    private final String version;
    private final String author;
    private final String description;
    private final String[] dependencies;

    public ModuleDescription(String mainClass, String name, String version, String author, String description, String[] dependencies) {
        this.mainClass = mainClass;
        this.name = name;
        this.version = version;
        this.author = author;
        this.description = description;
        this.dependencies = dependencies;
    }

    public String getMain() {
        return mainClass;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String[] getDependencies() {
        return dependencies;
    }
    
}
