dependencies {
    // compileOnly: SimpleCoreAPI is installed as a separate plugin, never shaded in.
    // The resulting jar contains neither SimpleCoreAPI nor kotlin-stdlib.
    compileOnly(project(":paper"))
    compileOnly(libs.paper.api)
}

tasks.jar {
    archiveBaseName = "ExamplePlugin"
}
