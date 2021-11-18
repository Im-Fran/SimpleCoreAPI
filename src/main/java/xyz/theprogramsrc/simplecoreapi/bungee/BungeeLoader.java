package xyz.theprogramsrc.simplecoreapi.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI;

public class BungeeLoader extends Plugin {
 
    @Override public void onEnable(){
        new SimpleCoreAPI();
    }
    
}
