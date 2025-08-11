import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val log4jVersion: String = rootProject.findProperty("log4j.version") as String
val simpleYamlVersion: String = rootProject.findProperty("simpleyaml.version") as String
val jetbrainsAnnotationsVersion: String = rootProject.findProperty("jetbrains-annotations.version") as String
val commonsIoVersion: String = rootProject.findProperty("commons-io.version") as String
val googleGsonVersion: String = rootProject.findProperty("google-gson.version") as String
val jsonVersion: String = rootProject.findProperty("json.version") as String
val zip4jVersion: String = rootProject.findProperty("zip4j.version") as String
val slf4jVersion: String = rootProject.findProperty("slf4j.version") as String

dependencies {
    /* Api */
    implementation(project(":build-info"))

    /* Logging Module */
    compileOnly("org.apache.logging.log4j:log4j-api:$log4jVersion")
    compileOnly("org.apache.logging.log4j:log4j-core:$log4jVersion")

    /* Files Module */
    implementation("me.carleslc.Simple-YAML:Simple-Yaml:$simpleYamlVersion")

    /* Global Depends */
    compileOnly("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
    compileOnly("commons-io:commons-io:$commonsIoVersion")
    compileOnly("com.google.code.gson:gson:$googleGsonVersion")
    compileOnly("org.json:json:$jsonVersion")
    compileOnly("net.lingala.zip4j:zip4j:$zip4jVersion")
    compileOnly("org.slf4j:slf4j-api:$slf4jVersion")
    compileOnly("org.slf4j:slf4j-simple:$slf4jVersion")

    testImplementation(kotlin("test"))
}

tasks {
    named<ShadowJar>("shadowJar") {
        dependsOn(":build-info:shadowJar")
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