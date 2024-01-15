import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"       // ShadowJar
    id("cl.franciscosolis.gradledotenv") version "1.0.1"        // .env support
    kotlin("jvm") version "1.9.21"                              // Kotlin
}

allprojects {
    apply {
        plugin("com.github.johnrengelman.shadow")
        plugin("cl.franciscosolis.gradledotenv")
        plugin("org.jetbrains.kotlin.jvm")
    }

    val projectVersion = (env["VERSION"] ?: "1.0.0")

    group = "xyz.theprogramsrc"
    version = projectVersion.replaceFirst("v", "").replace("/", "")
    description = "The best way to create a kotlin project."

    repositories {
        mavenLocal()
        mavenCentral()

        maven("https://s01.oss.sonatype.org/content/groups/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/releases/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://jitpack.io/")
    }
}

dependencies {
    implementation(project(":build-info", "shadow"))
    implementation(project(":simplecoreapi", "shadow"))
}

tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = "xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoaderKt"
        }

        mergeServiceFiles()
        exclude("**/*.kotlin_metadata")
        exclude("**/*.kotlin_builtins")

        archiveBaseName.set("SimpleCoreAPI")
        archiveClassifier.set("")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}