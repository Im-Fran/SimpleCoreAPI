import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.maven.publish) apply false
}

/** Subprojects published to Maven Central. `dist` and `example-plugin` are not. */
val publishedProjects = setOf("core", "paper", "spigot", "bungee")

val isSnapshot = version.toString().endsWith("-SNAPSHOT")

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "java-library")

    // Production and development live under different Maven Central namespaces.
    group = if (isSnapshot) "cl.franciscosolis.dev" else "cl.franciscosolis"

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget = JvmTarget.JVM_25
    }

    dependencies {
        "testImplementation"(kotlin("test"))
        "testImplementation"(rootProject.libs.junit.jupiter)
        "testRuntimeOnly"(rootProject.libs.junit.platform.launcher)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging { events("passed", "skipped", "failed") }
    }
}

configure(subprojects.filter { it.name in publishedProjects }) {
    apply(plugin = "com.vanniktech.maven.publish")

    extensions.configure<KotlinJvmProjectExtension> {
        // This is a public API consumed by third parties: explicit visibility and return types.
        explicitApi()
    }

    extensions.configure<BasePluginExtension> {
        archivesName = "simplecoreapi-${project.name}"
    }

    extensions.configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
        publishToMavenCentral()
        coordinates(group.toString(), "simplecoreapi-${project.name}", version.toString())

        // Only sign when a key is available, so publishToMavenLocal keeps working locally.
        if (providers.gradleProperty("signingInMemoryKey").isPresent) {
            signAllPublications()
        }

        pom {
            name = "SimpleCoreAPI ${project.name.replaceFirstChar { it.uppercase() }}"
            description = "Modular API for building Minecraft plugins in Kotlin"
            url = "https://github.com/Im-Fran/SimpleCoreAPI"
            licenses {
                license {
                    name = "GNU General Public License v3.0"
                    url = "https://www.gnu.org/licenses/gpl-3.0.txt"
                }
            }
            developers {
                developer {
                    id = "Im-Fran"
                    name = "Francisco Solis"
                    email = "f.solism@icloud.com"
                }
            }
            scm {
                url = "https://github.com/Im-Fran/SimpleCoreAPI"
                connection = "scm:git:git://github.com/Im-Fran/SimpleCoreAPI.git"
                developerConnection = "scm:git:ssh://git@github.com/Im-Fran/SimpleCoreAPI.git"
            }
        }
    }
}
