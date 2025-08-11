dependencies {
    /* Api */
    compileOnly(project(":build-info"))
    /* SimpleCoreAPI */
    implementation(project(":simplecoreapi", "shadow"))

    /* Runtimes */
    compileOnly("net.md-5:bungeecord-api:1.21-R0.1-SNAPSHOT")
}