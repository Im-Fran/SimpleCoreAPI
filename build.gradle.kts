
import com.github.jengelman.gradle.plugins.shadow.ShadowExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*

plugins {
    `maven-publish`

    id("io.github.goooler.shadow") version "8.1.8"                   // ShadowJar
    id("cl.franciscosolis.gradledotenv") version "1.0.1"                    // .env support
    kotlin("jvm") version "2.0.0"                                           // Kotlin
    id("org.jetbrains.dokka") version "1.9.20"                              // Dokka (Kotlin Docs)
    id("cl.franciscosolis.sonatype-central-upload") version "1.0.3"         // Sonatype Central Upload
    id("org.scm-manager.license") version "0.7.1"                           // License Header

}

/*
 * Project Build ID is based on GIT_COMMIT_SHORT_HASH if any,
 * otherwise we generate an UUIDv4 that will randomize its
 * contents and get the first 8 characters.
 */
val projectBuildId = env["GIT_COMMIT_SHORT_HASH"] ?: UUID.randomUUID().toString().replace("-", "").split("").shuffled().joinToString("").substring(0, 8)

/*
 * Project Version will be the environment variable version (or the one specified by us) if it's production,
 * otherwise it will be added '$projectBuildId'
 * (no snapshot because maven central does not support it)
 */
val projectVersion = "${env["VERSION"] ?: "1.0.0"}${if (env["ENV"] != "prod") "-$projectBuildId" else ""}"
/* Print out the current version */
println("This build version was '$projectVersion'")

val repo = "https://github.com/Im-Fran/SimpleCoreAPI"

/*
 * The groupId will be the package name in lowercase
 * if the environment is not production, we will add the environment name
 * (or 'dev' if it's not specified)
 */
val groupId = "cl.franciscosolis${if(env["ENV"] != "prod") ".${env["ENV"] ?: "dev"}" else ""}".lowercase()

group = groupId
version = projectVersion.replaceFirst("v", "").replace("/", "")
description = "The best way to create a kotlin project."

allprojects {
    apply {
        plugin("io.github.goooler.shadow")
        plugin("cl.franciscosolis.gradledotenv")
        plugin("org.jetbrains.kotlin.jvm")
    }

    group = rootProject.group
    version = rootProject.version
    description = rootProject.description

    repositories {
        mavenCentral()

        maven("https://s01.oss.sonatype.org/content/groups/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/releases/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://jitpack.io/")

        mavenLocal()
    }

    kotlin {
        compilerOptions.jvmTarget = JvmTarget.JVM_21
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        withSourcesJar()
        withJavadocJar()
    }

    tasks {
        jar {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        copy {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
}

subprojects {
    apply {
        plugin("org.scm-manager.license")
        plugin("org.jetbrains.dokka")
    }

    license {
        header(rootProject.file("LICENSE-HEADER"))
        include("**/src/main/**/*.kt")
        newLine(true)
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }

        named<DokkaTaskPartial>("dokkaHtmlPartial") {
            outputDirectory = layout.buildDirectory.dir("dokka")
            cacheRoot = file("${System.getProperty("user.home")}/.cache/dokka").apply {
                if(!exists()) mkdirs()
            }
        }
    }
}

dependencies {
    implementation(project(":build-info", "shadow"))
    implementation(project(":simplecoreapi", "shadow"))
}

tasks {
    named<ShadowJar>("shadowJar") {
        doLast {
            copy {
                from(archiveFile.get().asFile.absolutePath)
                into(rootProject.layout.buildDirectory.dir("libs"))
                rename { "simplecoreapi.jar" }
            }
        }

        manifest {
            attributes["Main-Class"] = "cl.franciscosolis.simplecoreapi.standalone.StandaloneLoaderKt"
        }

        mergeServiceFiles()
        exclude("**/*.kotlin_metadata")
        exclude("**/*.kotlin_builtins")

        archiveBaseName = "simplecoreapi"
        archiveClassifier = ""
    }

    register<Jar>("mergeSourcesJar") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveClassifier = "sources"
        from(subprojects.filter { subproject -> subproject.tasks.any { task -> task.name == "sourcesJar" } }
            .flatMap { subproject -> subproject.sourceSets.map { sourceSet -> sourceSet.allSource } })

        copy {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        archiveBaseName = "simplecoreapi"
    }

    dokkaHtmlMultiModule {
        dependsOn(":dokkaHtml")
        outputDirectory = rootProject.layout.buildDirectory.dir("dokka/")
    }

    register<Jar>("dokkaJavadocJar") {
        dependsOn(dokkaHtmlMultiModule)
        from(dokkaHtmlMultiModule.flatMap { dokkaTask -> dokkaTask.outputDirectory })
        archiveClassifier = "javadoc"
        archiveBaseName = "simplecoreapi"
    }

    sonatypeCentralUpload {
        dependsOn(named("publish"))

        username = env["SONATYPE_USERNAME"]
        password = env["SONATYPE_PASSWORD"]

        val publication = publishing.publications.named<MavenPublication>("shadow").orNull ?: error("Publication 'shadow' not found")
        archives = files(publication.artifacts?.map { artifact -> artifact.file })
        pom = file(layout.buildDirectory.file("publications/shadow/pom-default.xml"))

        signingKey = env["SIGNING_KEY"]
        signingKeyPassphrase = env["SIGNING_PASSWORD"]
        publicKey = env["PUBLIC_KEY"]
    }
}

publishing {
    repositories {
        if (env["ENV"] in listOf("prod", "dev")) {
            if (env["GITHUB_ACTOR"] != null && env["GITHUB_TOKEN"] != null) {
                maven {
                    name = "GithubPackages"
                    url = uri("https://maven.pkg.github.com/Im-Fran/SimpleCoreAPI")
                    credentials {
                        username = env["GITHUB_ACTOR"]
                        password = env["GITHUB_TOKEN"]
                    }
                }
            }
        }

        mavenLocal()
    }

    publications {
        create<MavenPublication>("shadow") {
            configure<ShadowExtension> {
                artifactId = rootProject.name.lowercase()

                component(this@create)
                artifact(tasks.named("dokkaJavadocJar"))
                artifact(tasks.named("mergeSourcesJar"))

                // Add contents from pom template
                pom {
                    name = rootProject.name
                    description = project.description
                    url = repo

                    licenses {
                        license {
                            name = "GNU GPL v3"
                            url = "${repo}/blob/master/LICENSE"
                        }
                    }

                    developers {
                        developer {
                            id = "Im-Fran"
                            name = "Francisco Solís"
                            email = "imfran@duck.com"
                        }
                    }

                    scm {
                        url = "$repo${if(repo.endsWith(".git")) "" else ".git"}"
                    }
                }
            }
        }
    }
}
