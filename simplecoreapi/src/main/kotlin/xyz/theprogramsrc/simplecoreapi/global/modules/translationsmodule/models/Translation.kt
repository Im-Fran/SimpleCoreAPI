package xyz.theprogramsrc.simplecoreapi.global.modules.translationsmodule.models

import xyz.theprogramsrc.simplecoreapi.global.modules.filesmodule.config.YmlConfig
import xyz.theprogramsrc.simplecoreapi.global.modules.filesmodule.extensions.folder
import xyz.theprogramsrc.simplecoreapi.global.modules.translationsmodule.managers.TranslationManager
import java.io.File

/**
 * Representation of a translation.
 * @param id The id of the translation
 * @param defaultValue The default value of the translation.
 * @param group The group (folder) of the translation. Defaults to "common"
 * @param language The language of the translation. (Defaults to "en")
 * @param mainColor The main color of the translation. (Defaults to null)
 * @param colors The colors to use in the translation replacing strings. Example (using color '&c'): '**test**' should return '&ctest'. Defaults to empty array.
 * @param autoRegister If the translation should be automatically registered. (Defaults to true) It is recommended to disable if you're going to initialize the same translation multiple times (for example inside a loop)
 */
data class Translation(
    val id: String,
    val defaultValue: String,
    val group: String = "common",
    val language: String = "en",
    val mainColor: String? = null,
    val colors: Array<String> = emptyArray(),
    val autoRegister: Boolean = true
) {

    init {
        if (autoRegister) {
            TranslationManager.instance.registerTranslations(group, this)
        }
    }

    /**
     * Translates this [Translation] to the current language.
     * @param language The language of the translation. Set to null to use the default language. Defaults to null
     * @param placeholders The placeholders to use in the translation replacing strings. Example (using placeholder id 'test' and value 'test_value'): '{test}' should return 'test_value'.
     *                      You can use '{}' or '%%' as placeholder identifiers like '{test}' or '%test%'. Defaults to empty map.
     * @return The translated string.
     */
    fun translate(language: String? = null, placeholders: Map<String, String> = emptyMap(), colorize: Boolean = true): String {
        val file = YmlConfig(File(File("SimpleCoreAPI/translations/${if (group.endsWith("/")) group else "$group/"}").folder(), (language ?: TranslationManager.getCurrentLanguage()) + ".lang")) // Get the file of the translation
        val mainColor = this.mainColor ?: "" // Get the main color of the translation
        var translation = mainColor.plus(
            if (file.has(id)) { // If the translation exists
                file.getString(id) // Get the translation from the file
            } else { // If the translation doesn't exist
                defaultValue // Get the default value
            }
        )
        for (i in colors.indices) { // For each color
            try {
                val color = colors[i] // Get the color
                val string = Regex("\\*\\*(.+?)\\*\\*").findAll(translation).first().groupValues[1] // Get the string to replace
                translation = translation.replaceFirst("**$string**", "$color$string$mainColor") // Replace the first match with the colorized string
            } catch (_: Exception) {
            } // Ignore errors
        }

        placeholders.forEach { (key, value) -> // For each placeholder
            translation = translation.replace("{$key}", value).replace("%$key%", value) // Replace the placeholder using %% and {}
        }

        return if (colorize) { // Return the translated string
            translation.replace("&", "§")
        } else {
            translation
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Translation

        if (id != other.id) return false
        if (defaultValue != other.defaultValue) return false
        if (!colors.contentEquals(other.colors)) return false
        if (group != other.group) return false
        if (language != other.language) return false
        if (mainColor != other.mainColor) return false
        if (autoRegister != other.autoRegister) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + defaultValue.hashCode()
        result = 31 * result + colors.contentHashCode()
        result = 31 * result + group.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + (mainColor?.hashCode() ?: 0)
        result = 31 * result + autoRegister.hashCode()
        return result
    }
}