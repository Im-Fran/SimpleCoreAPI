import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    /* Api */
    compileOnly(project(":build-info"))
    /* SimpleCoreAPI */
    implementation(project(":simplecoreapi", "shadow"))

    /* Runtimes */
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")

    /* UIs Module */
    implementation("com.github.cryptomorin:XSeries:11.2.0")
}

tasks {
    named<ShadowJar>("shadowJar") {
        relocate("com.cryptomorin", "cl.franciscosolis.simplecoreapi.bukkit.libs.xseries")
    }
}