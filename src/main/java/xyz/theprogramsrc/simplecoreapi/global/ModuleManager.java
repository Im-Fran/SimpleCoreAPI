package xyz.theprogramsrc.simplecoreapi.global;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jetbrains.annotations.Nullable;

import xyz.theprogramsrc.simplecoreapi.global.exceptions.InvalidModuleDescriptionException;
import xyz.theprogramsrc.simplecoreapi.global.exceptions.InvalidModuleException;
import xyz.theprogramsrc.simplecoreapi.global.exceptions.ModuleLoadException;
import xyz.theprogramsrc.simplecoreapi.global.object.Module;
import xyz.theprogramsrc.simplecoreapi.global.object.ModuleDescription;

public class ModuleManager {

    private static boolean isLoaded = false;
    private final Map<String, Module> modules;
    private final File modulesFolder;
    private ModuleManager(){
        this.modules = new LinkedHashMap<>();
        this.modulesFolder = new File("plugins/SimpleCoreAPI/modules");
        if(!modulesFolder.exists()){
            modulesFolder.mkdirs();
        }
    }

    public static ModuleManager init(){
        if(isLoaded) {
            throw new IllegalStateException("ModuleManager is already loaded!");
        }

        isLoaded = true;
        return new ModuleManager().load();
    }

    private ModuleManager load(){
        File[] files = this.modulesFolder.listFiles();
        if(files == null) files = new File[0];
        files = Arrays.stream(files).filter(file-> file.getName().endsWith(".jar")).toArray(File[]::new);
        for(File file : files){
            Properties props = this.loadDescription(file);
            if(props == null){
                throw new InvalidModuleDescriptionException("Failed to load module description for " + file.getName());
            }
            
            String[] required = new String[]{"main","name","version","author","description"};
            for(String req : required){
                if(!props.containsKey(req)){
                    throw new InvalidModuleDescriptionException("Missing required property " + req + " in module " + file.getName());
                }
            }

            ModuleDescription description = new ModuleDescription(
                props.getProperty("main"),
                props.getProperty("name"),
                props.getProperty("version"),
                props.getProperty("author"),
                props.getProperty("description"),
                (props.containsKey("dependencies") ? props.getProperty("dependencies") : "").split(",")
            );

            // Load jar file into classpath
            try(URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader())){
                Class<?> mainClass = loader.loadClass(description.getMain());
                if(!Module.class.isAssignableFrom(mainClass)){
                    throw new InvalidModuleException("The class " + description.getMain() + " must be extended to the Module class!");
                }
                
                Module module = (Module) mainClass.getConstructor(File.class, ModuleDescription.class).newInstance(file, description);
                module.onEnable();
                this.modules.put(description.getName(), module);
            }catch(Exception e){
                throw new ModuleLoadException("Failed to load module " + file.getName(), e);
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(()-> this.modules.values().stream().forEach(Module::onDisable)));
        return this;
    }

    @Nullable private Properties loadDescription(File from){
        // Search the file 'module.properties' inside the jar file
        try(JarFile jarFile = new JarFile(from)){
            JarEntry jarEntry = jarFile.getJarEntry("module.properties");
            if(jarEntry != null){
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                Properties properties = new Properties();
                properties.load(inputStream);
                return properties;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
}
