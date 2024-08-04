dependencies {
    /* Api */
    compileOnly(project(":build-info"))
    /* SimpleCoreAPI */
    implementation(project(":simplecoreapi"))

    /* Runtimes */
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")

    /* UIs Module */
    implementation("com.github.cryptomorin:XSeries:11.2.0")
}