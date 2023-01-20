import com.github.jengelman.gradle.plugins.shadow.ShadowExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("cl.franciscosolis.blossom-extended") version "1.3.1"

    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.dokka") version "1.7.20"
}

val env = emptyMap<String, String>().toMutableMap()
env.putAll(System.getenv())
if(project.rootProject.file(".env").exists()) {
    env.putAll(
        project.rootProject
            .file(".env")
            .inputStream()
            .bufferedReader()
            .readLines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .map { it.split("=") }
            .associate { it[0] to it[1] }
    )
}
val projectVersion = env["VERSION"] ?: "0.6.1-SNAPSHOT"

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
        withJavadocJar()
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
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

    dokkaHtml {
        outputDirectory.set(file(project.buildDir.absolutePath + "/dokka"))
    }
}

configurations {
    testImplementation {
        extendsFrom(configurations.compileOnly.get())
    }
}

val dokkaJavadocJar by tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        if (env["env"] == "prod") {
            if (env.containsKey("GITHUB_ACTOR") && env.containsKey("GITHUB_TOKEN")) {
                maven {
                    name = "GithubPackages"
                    url = uri("https://maven.pkg.github.com/TheProgramSrc/SimpleCoreAPI")
                    credentials {
                        username = env["GITHUB_ACTOR"]
                        password = env["GITHUB_TOKEN"]
                    }
                }
            }
        } else {
            mavenLocal()
        }
    }

    publications {
        create<MavenPublication>("shadow") {

            project.extensions.configure<ShadowExtension> {
                artifactId = "simplecoreapi"

                component(this@create)
                artifact(dokkaJavadocJar)
                artifact(tasks.kotlinSourcesJar)

                pom {
                    licenses {
                        license {
                            name.set("GNU GPL v3")
                            url.set("https://github.com/TheProgramSrc/SimpleCoreAPI/blob/master/LICENSE")
                        }
                    }

                    developers {
                        developer {
                            id.set("ImFran")
                            name.set("Francisco Solis")
                            email.set("imfran@duck.com")
                        }
                    }

                    scm {
                        url.set("https://github.com/TheProgramSrc/SimpleCoreAPI")
                    }
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

            username.set(env["SONATYPE_USERNAME"])
            password.set(env["SONATYPE_PASSWORD"])
        }
    }
}

tasks.withType<PublishToMavenRepository> {
    dependsOn(tasks.clean, tasks.test, tasks.kotlinSourcesJar, dokkaJavadocJar, tasks.jar, tasks.shadowJar)
}

tasks.withType<PublishToMavenLocal> {
    dependsOn(tasks.test, tasks.kotlinSourcesJar, tasks.jar, dokkaJavadocJar, tasks.shadowJar)
}

tasks.register("env-test") {
    doLast {
        println("env: ${env["env"]}")
        println("SONATYPE_USERNAME: ${env["SONATYPE_USERNAME"]}")
        println("SONATYPE_PASSWORD: ${env["SONATYPE_PASSWORD"]}")
    }
}