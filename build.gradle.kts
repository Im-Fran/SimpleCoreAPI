import com.github.jengelman.gradle.plugins.shadow.ShadowExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.util.*

plugins {
    `maven-publish`

    id("com.github.johnrengelman.shadow") version "8.1.1"       // ShadowJar
    id("cl.franciscosolis.gradledotenv") version "1.0.1"        // .env support
    kotlin("jvm") version "1.9.22"                              // Kotlin
    id("org.jetbrains.dokka") version "1.9.10"                  // Dokka (Kotlin Docs)
}

/*
    The project version can be:
    - environment variable VERSION or the manually added version
    - If the environment variable ENV is set to dev, the project
    version will have appended the git commit short hash + SNAPSHOT
*/
val projectVersion = (env["VERSION"] ?: "1.0.0") + (if(env["ENV"] == "dev") "-${env["GIT_COMMIT_SHORT_HASH"] ?: UUID.randomUUID().toString().replace("-", "").split("").shuffled().joinToString("").substring(0,8)}-SNAPSHOT" else "")
println("This build version was '$projectVersion'")

allprojects {
    apply {
        plugin("com.github.johnrengelman.shadow")
        plugin("cl.franciscosolis.gradledotenv")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.dokka")
    }

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

subprojects {
    apply(plugin = "org.jetbrains.dokka")

    tasks {
        java {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
            withSourcesJar()
            withJavadocJar()
        }

        named<DokkaTaskPartial>("dokkaHtmlPartial") {
            outputDirectory = layout.buildDirectory.dir("dokka")
        }
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

    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        withSourcesJar()
        withJavadocJar()
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    copy {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    register<Jar>("mergeSourcesJar") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveClassifier = "sources"
        from(subprojects.filter { subproject -> subproject.tasks.any { task -> task.name == "sourcesJar" } }.flatMap { subproject -> subproject.sourceSets.map { sourceSet -> sourceSet.allSource } })

        copy {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }

    dokkaHtmlMultiModule {
        outputDirectory = rootProject.layout.buildDirectory.dir("dokka/")
    }

    register<Jar>("dokkaJavadocJar") {
        dependsOn(dokkaHtmlMultiModule)
        from(dokkaHtmlMultiModule.flatMap { dokkaTask -> dokkaTask.outputDirectory })
        archiveClassifier.set("javadoc")
    }
}

publishing {
    repositories {
        if (env["ENV"] == "prod" || env["ENV"] == "dev") {
            if (env["GITHUB_ACTOR"] != null && env["GITHUB_TOKEN"] != null) {
                maven {
                    name = "GithubPackages"
                    url = uri("https://maven.pkg.github.com/TheProgramSrc/SimpleCoreAPI")
                    credentials {
                        username = env["GITHUB_ACTOR"]
                        password = env["GITHUB_TOKEN"]
                    }
                }
            }

            if(env["SONATYPE_USERNAME"] != null && env["SONATYPE_PASSWORD"] != null) {
                maven {
                    name = "Sonatype"
                    url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                    credentials {
                        username = env["SONATYPE_USERNAME"]
                        password = env["SONATYPE_PASSWORD"]
                    }
                }
            }
        }

        if(env["ENV"] != "prod") {
            mavenLocal()
        }
    }

    publications {
        create<MavenPublication>("shadow") {

            project.extensions.configure<ShadowExtension> {
                artifactId = rootProject.name.lowercase()

                component(this@create)
                artifact(tasks.named("dokkaJavadocJar"))
                artifact(tasks.named("mergeSourcesJar"))

                pom {
                    name.set(rootProject.name)
                    description.set(project.description)
                    url.set("https://github.com/TheProgramSrc/SimpleCoreAPI")

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
