[![](https://jitci.com/gh/TheProgramSrc/SimpleCoreAPI/svg)](https://jitci.com/gh/TheProgramSrc/SimpleCoreAPI)
[![](https://jitpack.io/v/TheProgramSrc/SimpleCoreAPI.svg)](https://jitpack.io/#TheProgramSrc/SimpleCoreAPI)
![Maven Central Version](https://img.shields.io/maven-central/v/cl.franciscosolis/simplecoreapi?label=Maven+Central)
[![Dokka](https://javadoc.io/badge2/cl.franciscosolis/simplecoreapi/Dokka.svg?logo=Kotlin&color=7F52FF)](https://javadoc.io/doc/cl.franciscosolis/simplecoreapi)

# SimpleCoreAPI
_The best way to create a plugin_<br>

This is a library coded in [Kotlin](https://github.com/JetBrains/Kotlin) to help kotlin developers create [Spigot](https://spigotmc.org), [BungeeCord](https://github.com/SpigotMC/BungeeCord) and [Velocity](https://github.com/PaperMC/Velocity) Plugins.
It also supports creating Standalone applications (but only with the global modules, because those do not require any other software to work).

## Included Libraries
* [Kotlin](https://github.com/JetBrains/Kotlin) ([License](https://github.com/JetBrains/Kotlin/blob/master/license/))
* [log4j](https://github.com/apache/logging-log4j2) ([License](https://github.com/apache/logging-log4j2/blob/2.x/LICENSE.txt))
* [Simple-YAML](https://github.com/Carleslc/Simple-YAML) ([License](https://github.com/Carleslc/Simple-YAML/blob/master/LICENSE))
* [XSeries](https://github.com/cryptomorin/XSeries) ([License](https://github.com/cryptomorin/XSeries/blob/master/LICENSE.txt))
* [JetBrains Annotations](https://github.com/JetBrains/java-annotations) ([License](https://github.com/JetBrains/java-annotations/blob/master/LICENSE.txt))
* [commons-io](https://github.com/apache/commons-io) ([License](https://www.apache.org/licenses/LICENSE-2.0))
* [Google Gson](https://github.com/google/gson) ([License](https://github.com/google/gson/blob/main/LICENSE))
* [JSON-java](https://github.com/stleary/JSON-java) ([License](https://github.com/stleary/JSON-java/blob/master/LICENSE))
* [Zip4J](https://github.com/srikanth-lingala/zip4j) ([License](https://github.com/srikanth-lingala/zip4j/blob/master/LICENSE))
* [SLF4J](https://github.com/qos-ch/slf4j) ([License](https://github.com/qos-ch/slf4j/blob/master/LICENSE.txt))

## Where is the documentation?
The docs can be found [here](https://im-fran.github.io/SimpleCoreAPI/) (it's a Dokka Resource), everything is documented through the Kotlin Docs (Similar to JavaDocs but for Kotlin :p )

## How does this work?
Ok, so we have multiple types of initializers for different software, Spigot, Bungee, Velocity and Standalone.

The Standalone system is meant to be executed without any extra software, like desktop applications or command line tools.
Only the global modules are available in this mode.

## Ok, and how do I use the Standalone mode?
In the Standalone system you will naturally shade SimpleCoreAPI and use the `xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader` as your main class.<br/>
Later you will mark your main class with the `@EntryPoint` annotation. Then SimpleCoreAPI will load all modules, then execute the `@EntryPoint` class.

## How do I use the other modes?
Depending on the software you're using you'll need to mark SimpleCoreAPI and the modules you need as `dependency` (NOT `soft-dependency`) so the system will load all 
modules and files you need before your plugin is loaded.
