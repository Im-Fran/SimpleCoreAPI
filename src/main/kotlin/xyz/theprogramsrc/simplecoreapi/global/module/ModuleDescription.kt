package xyz.theprogramsrc.simplecoreapi.global.module

/**
 * Represents a Module Description
 * @param mainClass The main class of the module
 * @param name The name of the module
 * @param version The version of the module
 * @param author The author of the module
 * @param description The description of the module
 * @param repositoryId The identifier of this artifact in the repository (usually the name in lowercase)
 * @param dependencies The dependencies of the module (must be the name of the module)
 * @param githubRepository The GitHub repository of the module. (Used for the update checker and auto updater)
 * @param disableStandalone If true the module won't be loaded if it's in standalone mode.
 */
data class ModuleDescription(
    val mainClass: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val repositoryId: String,
    val dependencies: Array<String>,
    val githubRepository: String,
    val disableStandalone: Boolean = false,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModuleDescription

        if (mainClass != other.mainClass) return false
        if (name != other.name) return false
        if (version != other.version) return false
        if (author != other.author) return false
        if (description != other.description) return false
        if (repositoryId != other.repositoryId) return false
        if (!dependencies.contentEquals(other.dependencies)) return false
        if (githubRepository != other.githubRepository) return false
        if (disableStandalone != other.disableStandalone) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mainClass.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + repositoryId.hashCode()
        result = 31 * result + dependencies.contentHashCode()
        result = 31 * result + githubRepository.hashCode()
        result = 31 * result + disableStandalone.hashCode()
        return result
    }

}
