import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

tasks {
    named<ShadowJar>("shadowJar") {
        relocate("org.intellij", "cl.franciscosolis.simplecoreapi.libs.intellij")
        relocate("org.jetbrains", "cl.franciscosolis.simplecoreapi.libs.jetbrains")
    }
}