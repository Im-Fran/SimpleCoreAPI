plugins {
    id("io.papermc.paperweight.userdev") version "1.7.7"        // Paperweight
}

val xseriesVersion: String = rootProject.findProperty("xseries.version") as String

dependencies {
    /* Api */
    compileOnly(project(":build-info"))
    /* SimpleCoreAPI */
    implementation(project(":simplecoreapi", "shadow"))

    /* Runtimes */
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")

    /* UIs Module */
    compileOnly("com.github.cryptomorin:XSeries:$xseriesVersion")
}

tasks {
    assemble {
        dependsOn(named("reobfJar"))
    }
}