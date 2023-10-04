package xyz.theprogramsrc.simplecoreapi.global.dependencydownloader

import xyz.theprogramsrc.simplecoreapi.global.models.Dependency
import xyz.theprogramsrc.simplecoreapi.global.models.Repository
import xyz.theprogramsrc.simplecoreapi.global.utils.folder
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.security.MessageDigest
import java.util.jar.JarFile
import java.util.logging.Logger

class DependencyDownloader {

    companion object { lateinit var instance: DependencyDownloader }

    private val logger = Logger.getLogger("DependencyDownloader")
    private val dependencies = mutableListOf<Dependency>()
    private val librariesFolder = File("libraries/DependencyDownloader/").folder()
    private val repositories = mutableListOf(
        Repository("https://s01.oss.sonatype.org/content/groups/public/"),
        Repository("https://oss.sonatype.org/content/repositories/snapshots/"),
        Repository("https://oss.sonatype.org/content/repositories/releases/"),
        Repository("https://oss.sonatype.org/content/groups/public/"),
        Repository("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"),
        Repository("https://repo.papermc.io/repository/maven-public/"),
        Repository("https://repo.codemc.org/repository/maven-public/"),
        Repository("https://jitpack.io/")
    )
    private val loadedDependencies = mutableListOf<String>()
    private val digest = MessageDigest.getInstance("MD5")

    init {
        instance = this
    }

    /**
     * Adds a dependency to load
     * @param dependency The [Dependency] to load
     */
    fun addDependency(dependency: Dependency) {
        val found = dependencies.find { it.group == dependency.group && it.artifactId == dependency.artifactId }
        if(found == null) {
            dependencies.add(dependency)
            return
        }

        if(found.version != dependency.version){
            logger.warning("Dependency '${found.group}:${found.artifactId}' already exists with version '${found.version}'!")
        }
    }

    /**
     * Adds a [Repository] to the list of repositories.
     * @param repository The [Repository] to add. (Must be a valid [Repository] with the Sonatype Nexus Repository Manager Software)
     */
    fun addRepository(repository: Repository) {
        val add = try {
            URL(repository.url)
            true
        } catch (e: Exception) {
            false
        }

        if(!add) {
            logger.severe("Repository ${repository.url} must hava a valid url!")
            return
        }

        if(repositories.any { it.url == repository.url }) {
            logger.warning("Repository ${repository.url} already exists!")
            return
        }

        repositories.add(repository)
    }

    /**
     * Loads a [Dependency] into the classpath if it is not already loaded
     *
     * @param dependency The [Dependency] to load
     * @return The [Dependency] file if is successfully loaded, null otherwise
     */
    fun loadDependency(dependency: Dependency): File? {
        addDependency(dependency)
        val file = File(File(librariesFolder, dependency.group.replace(".", "/")).folder(), "${dependency.artifactId}-${dependency.version}.jar")
        if(isLoaded(dependency)) return file
        if(!file.exists()){
            val repo = repositories.firstOrNull { it.findArtifact(dependency) != null } ?: return null
            val artifactUrl = repo.findArtifact(dependency) ?: return null
            digest.reset()
            val downloadBytes = URL(artifactUrl).readBytes()
            if(dependency.md5Hash != null){
                val downloadMd5 = digest.digest(downloadBytes).joinToString("") { "%02x".format(it) }
                if(downloadMd5 != dependency.md5Hash){
                    logger.severe("MD5 mismatch for ${dependency.group}:${dependency.artifactId}! Expected: '${dependency.md5Hash}', Got: '$downloadMd5'")
                    return null
                }
            }

            file.writeBytes(downloadBytes)
        }

        if(ClasspathLoader.loadIntoClasspath(file)){
            loadedDependencies.add(dependency.group + ":" + dependency.artifactId)
            logger.info("Loaded dependency ${dependency.group}:${dependency.artifactId}")
            return file
        } else {
            logger.severe("Failed to load dependency ${dependency.group}:${dependency.artifactId} into the classpath!")
        }
        return null
    }

    /**
     * Load all the dependencies into the classpath.
     * If any dependency is already loaded it'll be skipped
     */
    fun loadDependencies(): Unit = dependencies.forEach(this::loadDependency)

    /**
     * Checks if the given [Dependency] is already loaded
     * @return True if the dependency is loaded, false otherwise
     */
    fun isLoaded(dependency: Dependency): Boolean = loadedDependencies.contains(dependency.group + ":" + dependency.artifactId)
}

object ClasspathLoader {

    /**
     * Loads a jar file into the classpath
     * @param file The jar file to load
     */
    fun loadIntoClasspath(file: File): Boolean = try {
        require(file.isFile && file.extension == "jar") {
            "The input file must be a JAR file."
        }

        URLClassLoader(arrayOf(file.toURI().toURL()), this.javaClass.classLoader).use { loader ->
            val jarFile = JarFile(file)
            jarFile.entries().asSequence()
                .filter { it.name.endsWith(".class") && !it.name.startsWith("META-INF/") }
                .map { it.name.removeSuffix(".class").replace('/', '.') }
                .forEach { loader.loadClass(it) }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}