package xyz.theprogramsrc.simplecoreapi.spigot;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI;

public class SpigotLoader extends JavaPlugin {

    @Override public void onEnable(){
        new SimpleCoreAPI();
    }
    
}
