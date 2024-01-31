[![](https://jitci.com/gh/TheProgramSrc/SimpleCoreAPI/svg)](https://jitci.com/gh/TheProgramSrc/SimpleCoreAPI)
[![](https://jitpack.io/v/TheProgramSrc/SimpleCoreAPI.svg)](https://jitpack.io/#TheProgramSrc/SimpleCoreAPI)
![Maven Central Version](https://img.shields.io/maven-central/v/cl.franciscosolis/simplecoreapi?label=Maven+Central)
[![Dokka](https://javadoc.io/badge2/cl.franciscosolis/simplecoreapi/Dokka.svg?logo=Kotlin&color=7F52FF)](https://javadoc.io/doc/cl.franciscosolis/simplecoreapi)

# SimpleCoreAPI
_The best way to create a plugin_<br>

[![Discord](https://i.imgur.com/J1XhmMd.png)](https://go.franciscosolis.cl/discord)
[![Terms of Service](https://i.imgur.com/4tFAGtE.png)](https://go.theprogramsrc.xyz/tos)

## Included Libraries
* [Kotlin](https://github.com/JetBrains/Kotlin) ([License](https://github.com/JetBrains/Kotlin/blob/master/license/))
* [JetBrains Annotations](https://github.com/JetBrains/java-annotations) ([License](https://github.com/JetBrains/java-annotations/blob/master/LICENSE.txt))
* [commons-io](https://github.com/apache/commons-io) ([License](https://www.apache.org/licenses/LICENSE-2.0))
* [Google Gson](https://github.com/google/gson) ([License](https://github.com/google/gson/blob/master/LICENSE))
* [Zip4J](https://github.com/srikanth-lingala/zip4j) ([License](https://github.com/srikanth-lingala/zip4j/blob/master/LICENSE))

## Links
* Become a [Patron](https://go.theprogramsrc.xyz/patreon) and support us!
* [Donate](https://go.theprogramsrc.xyz/donate) to support us!

## Where is the documentation?
This can be found [here](https://docs.theprogramsrc.xyz/SimpleCoreAPI/) (it's a Dokka Resource), everything is documented through the Kotlin Docs (Similar to JavaDocs but for Kotlin :p )

## How does this work?
Ok, so we have multiple types of initializers for different software, Spigot, Bungee, Velocity and Standalone.

The Standalone system works by loading all jars under the modules/ folder into the current classpath.<br/>
The other kinds of software will use their embedded plugin system to avoid difficult tasks, like sorting modules to be loaded.

## Ok, and how do I use the Standalone mode?
In the Standalone system you will naturally shade SimpleCoreAPI and use the `xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader` as your main class.<br/>
Later you will mark your main class with the `@EntryPoint` annotation. Then SimpleCoreAPI will load all modules, then execute the `@EntryPoint` class.

## How do I use the other modes?
Depending on the software you're using you'll need to mark SimpleCoreAPI and the modules you need as `dependency` (NOT `soft-dependency`) so the system will load all 
modules and files you need before your plugin is loaded.
