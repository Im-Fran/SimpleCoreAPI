plugins {
    id("io.papermc.paperweight.userdev") version "1.7.1"        // Paperweight
}

dependencies {
    /* Api */
    compileOnly(project(":build-info"))
    /* SimpleCoreAPI */
    implementation(project(":simplecoreapi"))

    /* Runtimes */
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")

    /* UIs Module */
    implementation("com.github.cryptomorin:XSeries:11.2.0")
}

tasks {
    assemble {
        dependsOn(named("reobfJar"))
    }
}