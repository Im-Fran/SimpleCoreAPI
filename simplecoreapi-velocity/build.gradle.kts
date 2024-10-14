dependencies {
    /* Api */
    compileOnly(project(":build-info"))
    /* SimpleCoreAPI */
    implementation(project(":simplecoreapi", "shadow"))

    /* Runtimes */
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
}