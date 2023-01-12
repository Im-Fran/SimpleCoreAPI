import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import groovy.util.Node
import groovy.util.NodeList
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("cl.franciscosolis.blossom-extended") version "1.3.1"

    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.dokka") version "1.7.20"
}

val env = System.getenv()
val projectVersion = env["VERSION"] ?: "0.5.0-SNAPSHOT"

group = "xyz.theprogramsrc"
version = projectVersion.replaceFirst("v", "").replace("/", "")
description = "The best way to create a plugin"

repositories {
    mavenCentral()

    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/releases/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")

    implementation("org.jetbrains:annotations:24.0.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.lingala.zip4j:zip4j:2.11.2")

    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

blossom {
    System.getenv()
    replaceToken("@name@", rootProject.name)
    replaceToken("@version@", project.version.toString())
    replaceToken("@description@", project.description)
    replaceToken("@git_short@", env["GIT_COMMIT_SHORT_HASH"] ?: "unknown")
    replaceToken("@git_full@", env["GIT_COMMIT_LONG_HASH"] ?: "unknown")
}

tasks {
    named<ShadowJar>("shadowJar") {
        relocate("org.apache.commons", "xyz.theprogramsrc.simplecoreapi.libs.apache.commons")
        relocate("org.checkerframework", "xyz.theprogramsrc.simplecoreapi.libs.checkerframework")
        relocate("org.intellij", "xyz.theprogramsrc.simplecoreapi.libs.intellij")
        relocate("org.jetbrains", "xyz.theprogramsrc.simplecoreapi.libs.jetbrains")
        relocate("javax.annotation", "xyz.theprogramsrc.simplecoreapi.libs.annotation")
        relocate("net.lingala.zip4j", "xyz.theprogramsrc.simplecoreapi.libs.zip4j")

        mergeServiceFiles()
        exclude("**/*.kotlin_metadata")
        exclude("**/*.kotlin_builtins")

        archiveBaseName.set("SimpleCoreAPI")
        archiveClassifier.set("")
    }

    test {
        useJUnitPlatform()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        withSourcesJar()
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    withType<Jar>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    withType<Copy>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    named<DokkaTask>("dokkaHtml"){
        outputDirectory.set(file(project.buildDir.absolutePath + "/dokka"))
    }
}

configurations {
    testImplementation {
        extendsFrom(configurations.compileOnly.get())
    }
}

publishing {
    repositories {
        if(env["env"] == "prod") {
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/TheProgramSrc/SimpleCoreAPI")
                credentials {
                    username = env["GITHUB_ACTOR"]
                    password = env["GITHUB_TOKEN"]
                }
            }
        } else {
            mavenLocal()
        }
    }

    publications {
        create<MavenPublication>("mavenKotlin") {
            artifactId = "simplecoreapi"

            from(components["java"])

            pom.withXml {
                asNode().appendNode("packaging", "jar")
                ((asNode().get("dependencies") as NodeList?)?.firstOrNull() as Node?)?.let { node ->
                    asNode().remove(node)
                }
            }

        }
    }
}

tasks.publish {
    dependsOn(tasks.clean, tasks.test, tasks.jar, tasks.shadowJar)
}