import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    /* Api */
    compileOnly(project(":build-info"))

    /* Runtimes */
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.20-R0.3")
    compileOnly("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")

    /* Logging Module */
    implementation("org.apache.logging.log4j:log4j-api:2.21.0")
    implementation("org.apache.logging.log4j:log4j-core:2.21.0")

    /* Files Module */
    implementation("me.carleslc.Simple-YAML:Simple-Yaml:1.8.4")

    /* UIs Module */
    implementation("com.github.cryptomorin:XSeries:9.8.0")

    /* Global Depends */
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("commons-io:commons-io:2.15.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.json:json:20231013")
    implementation("net.lingala.zip4j:zip4j:2.11.5")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:slf4j-simple:2.0.9")

    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
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

    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    compileJava {
        options.encoding = "UTF-8"
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