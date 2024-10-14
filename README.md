### Production Badges
![Maven Central Version (Prod)](https://img.shields.io/maven-central/v/cl.franciscosolis/simplecoreapi?label=Maven+Central+(Prod)&color=blue)
[![Dokka](https://javadoc.io/badge2/cl.franciscosolis/simplecoreapi/Dokka.svg?logo=Kotlin&color=7F52FF)](https://javadoc.io/doc/cl.franciscosolis/simplecoreapi)

### Dev Badges
![Maven Central Version (Dev)](https://img.shields.io/maven-central/v/cl.franciscosolis.dev/simplecoreapi?label=Maven+Central+(Dev)&color=orange)
[![Dokka](https://javadoc.io/badge2/cl.franciscosolis.dev/simplecoreapi/Dokka.svg?logo=Kotlin&color=7F52FF)](https://javadoc.io/doc/cl.franciscosolis.dev/simplecoreapi)

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
Ok, so we have multiple types of initializers for different software, Bukkit (for bukkit based runtimes), Paper (for paper based runtimes), Bungee, Velocity and Standalone.

Every package has its own abilities for their own runtimes, for example the paper package will take advantage of the paper utilities, the bukkit package will take advantage of the bukkit utilities, etc.
The Standalone package its meant to be used for standalone applications, it has the global modules and the global utilities, which are utilities that do not require any other software to work.

## How do I use the packages?
Depending on the software you're using you'll need to mark SimpleCoreAPI and the modules you need as `dependency` (NOT `soft-dependency`) so the system will load all 
modules and files you need before your plugin is loaded.

Let's take a look to the different packages
<details>
<summary>Bukkit</summary>

In order to use the Bukkit package you'll need to add the following package:

![Maven Central Version (Prod)](https://img.shields.io/maven-central/v/cl.franciscosolis/simplecoreapi-bukkit?label=Maven+Central+(Prod)&color=blue)
![Maven Central Version (Dev)](https://img.shields.io/maven-central/v/cl.franciscosolis.dev/simplecoreapi-bukkit?label=Maven+Central+(Dev)&color=orange)
```groovy
dependencies {
    compileOnly("cl.franciscosolis:simplecoreapi-bukkit:VERSION")
}
```

You'll also need to make sure to include the runnable (`simplecoreapi-bukkit.jar`) in your `plugins/` folder, so make sure to also tell your users to download it from the [Releases Page](https://github.com/Im-Fran/SimpleCoreAPI/releases/latest).
That's all you need to know in order to use the API.
</details>

<details>
<summary>Paper</summary>

In order to use the Paper package you'll need to add the following package:

![Maven Central Version (Prod)](https://img.shields.io/maven-central/v/cl.franciscosolis/simplecoreapi-paper?label=Maven+Central+(Prod)&color=blue)
![Maven Central Version (Dev)](https://img.shields.io/maven-central/v/cl.franciscosolis.dev/simplecoreapi-paper?label=Maven+Central+(Dev)&color=orange)
```groovy
dependencies {
    compileOnly("cl.franciscosolis:simplecoreapi-paper:VERSION")
}
```

You'll also need to make sure to include the runnable (`simplecoreapi-paper.jar`) in your `plugins/` folder, so make sure to also tell your users to download it from the [Releases Page](https://github.com/Im-Fran/SimpleCoreAPI/releases/latest).
That's all you need to know in order to use the API.
</details>

<details>
<summary>Bungee</summary>

In order to use the Bungee package you'll need to add the following package:

![Maven Central Version (Prod)](https://img.shields.io/maven-central/v/cl.franciscosolis/simplecoreapi-bungee?label=Maven+Central+(Prod)&color=blue)
![Maven Central Version (Dev)](https://img.shields.io/maven-central/v/cl.franciscosolis.dev/simplecoreapi-bungee?label=Maven+Central+(Dev)&color=orange)
```groovy
dependencies {
    compileOnly("cl.franciscosolis:simplecoreapi-bungee:VERSION")
}
```

You'll also need to make sure to include the runnable (`simplecoreapi-bungee.jar`) in your `plugins/` folder, so make sure to also tell your users to download it from the [Releases Page](https://github.com/Im-Fran/SimpleCoreAPI/releases/latest).
That's all you need to know in order to use the API.
</details>

<details>
<summary>Velocity</summary>

In order to use the Velocity package you'll need to add the following package:

![Maven Central Version (Prod)](https://img.shields.io/maven-central/v/cl.franciscosolis/simplecoreapi-velocity?label=Maven+Central+(Prod)&color=blue)
![Maven Central Version (Dev)](https://img.shields.io/maven-central/v/cl.franciscosolis.dev/simplecoreapi-velocity?label=Maven+Central+(Dev)&color=orange)
```groovy
dependencies {
    compileOnly("cl.franciscosolis:simplecoreapi-velocity:VERSION")
}
```

You'll also need to make sure to include the runnable (`simplecoreapi-velocity.jar`) in your `plugins/` folder, so make sure to also tell your users to download it from the [Releases Page](https://github.com/Im-Fran/SimpleCoreAPI/releases/latest).
That's all you need to know in order to use the API.
</details>

<details>
<summary>Standalone</summary>

In order to use the Standalone package you'll need to add the following package:

![Maven Central Version (Prod)](https://img.shields.io/maven-central/v/cl.franciscosolis/simplecoreapi?label=Maven+Central+(Prod)&color=blue)
![Maven Central Version (Dev)](https://img.shields.io/maven-central/v/cl.franciscosolis.dev/simplecoreapi?label=Maven+Central+(Dev)&color=orange)
```groovy
dependencies {
    implementation("cl.franciscosolis:simplecoreapi:VERSION")
}
```
</details>