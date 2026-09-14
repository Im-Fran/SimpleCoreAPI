plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":paper"))
    implementation(project(":spigot"))
    implementation(project(":bungee"))
}

// A single jar serves all three platforms: Paper reads paper-plugin.yml and ignores
// plugin.yml, Spigot reads plugin.yml and ignores paper-plugin.yml, BungeeCord reads bungee.yml.
tasks.processResources {
    val descriptors = mapOf("version" to project.version.toString())
    inputs.properties(descriptors)
    filesMatching(listOf("paper-plugin.yml", "plugin.yml", "bungee.yml")) {
        expand(descriptors)
    }
}

tasks.shadowJar {
    archiveBaseName = "SimpleCoreAPI"
    archiveClassifier = ""

    // Avoids clashing with the copies the server already ships.
    relocate("org.yaml.snakeyaml", "cl.franciscosolis.simplecoreapi.libs.snakeyaml")
    relocate("com.google.gson", "cl.franciscosolis.simplecoreapi.libs.gson")

    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/maven/**")
    mergeServiceFiles()
}

// Only the universal jar matters, not the subproject's empty jar.
tasks.jar { enabled = false }
tasks.build { dependsOn(tasks.shadowJar) }
