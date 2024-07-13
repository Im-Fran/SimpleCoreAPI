import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    /* Api */
    compileOnly(project(":build-info"))

    /* Runtimes */
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.21-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    /* Logging Module */
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")

    /* Files Module */
    implementation("me.carleslc.Simple-YAML:Simple-Yaml:1.8.4")

    /* UIs Module */
    implementation("com.github.cryptomorin:XSeries:11.2.0")

    /* Global Depends */
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("commons-io:commons-io:2.16.1")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.json:json:20240303")
    implementation("net.lingala.zip4j:zip4j:2.11.5")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
}

tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = "cl.franciscosolis.simplecoreapi.standalone.StandaloneLoaderKt"
        }

        relocate("org.apache.commons", "cl.franciscosolis.simplecoreapi.libs.apache.commons")
        relocate("org.checkerframework", "cl.franciscosolis.simplecoreapi.libs.checkerframework")
        relocate("org.intellij", "cl.franciscosolis.simplecoreapi.libs.intellij")
        relocate("org.jetbrains", "cl.franciscosolis.simplecoreapi.libs.jetbrains")
        relocate("javax.annotation", "cl.franciscosolis.simplecoreapi.libs.annotation")
        relocate("net.lingala.zip4j", "cl.franciscosolis.simplecoreapi.libs.zip4j")
        relocate("org.slf4j", "cl.franciscosolis.simplecoreapi.libs.sl4fj")

        mergeServiceFiles()
        exclude("**/*.kotlin_metadata")
        exclude("**/*.kotlin_builtins")

        archiveBaseName.set("SimpleCoreAPI")
        archiveClassifier.set("")
    }

    test {
        useJUnitPlatform()
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    copy {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

configurations {
    testImplementation {
        extendsFrom(configurations.compileOnly.get())
    }
}