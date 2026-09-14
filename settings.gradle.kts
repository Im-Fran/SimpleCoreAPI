plugins {
    // Automatically downloads JDK 25 when it is not installed on the machine.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "SimpleCoreAPI"

include("core", "paper", "spigot", "bungee", "dist", "example-plugin")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://central.sonatype.com/repository/maven-snapshots/")
    }
}
