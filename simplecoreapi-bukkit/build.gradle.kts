import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val xseriesVersion: String = rootProject.findProperty("xseries.version") as String

dependencies {
    /* Api */
    compileOnly(project(":build-info"))
    /* SimpleCoreAPI */
    implementation(project(":simplecoreapi", "shadow"))

    /* Runtimes */
    compileOnly("org.spigotmc:spigot-api:1.21.3-R0.1-SNAPSHOT")

    /* UIs Module */
    compileOnly("com.github.cryptomorin:XSeries:$xseriesVersion")
}

tasks {
    named<ShadowJar>("shadowJar") {
        relocate("com.cryptomorin", "cl.franciscosolis.simplecoreapi.bukkit.libs.xseries")
    }
}