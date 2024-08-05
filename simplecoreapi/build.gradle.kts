import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    /* Api */
    implementation(project(":build-info"))

    /* Logging Module */
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")

    /* Files Module */
    implementation("me.carleslc.Simple-YAML:Simple-Yaml:1.8.4")

    /* UIs Module */
    implementation("com.github.cryptomorin:XSeries:11.2.0")

    /* Global Depends */
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("commons-io:commons-io:2.16.1")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.json:json:20240303")
    implementation("net.lingala.zip4j:zip4j:2.11.5")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
}

tasks {
    named<ShadowJar>("shadowJar") {
        relocate("org.yaml", "cl.franciscosolis.simplecoreapi.libs.yaml")
        relocate("org.simpleyaml", "cl.franciscosolis.simplecoreapi.libs.simpleyaml")
        relocate("org.json", "cl.franciscosolis.simplecoreapi.libs.json")
        relocate("org.apache", "cl.franciscosolis.simplecoreapi.libs.apache")
        relocate("org.checkerframework", "cl.franciscosolis.simplecoreapi.libs.checkerframework")
        relocate("org.intellij", "cl.franciscosolis.simplecoreapi.libs.intellij")
        relocate("org.jetbrains", "cl.franciscosolis.simplecoreapi.libs.jetbrains")
        relocate("javax.annotation", "cl.franciscosolis.simplecoreapi.libs.annotation")
        relocate("net.lingala.zip4j", "cl.franciscosolis.simplecoreapi.libs.zip4j")
        relocate("org.slf4j", "cl.franciscosolis.simplecoreapi.libs.sl4fj")
        relocate("com.google", "cl.franciscosolis.simplecoreapi.libs.google")
    }

    test {
        useJUnitPlatform()
    }
}

configurations {
    testImplementation {
        extendsFrom(configurations.compileOnly.get())
    }
}