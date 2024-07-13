/*
 * SimpleCoreAPI - Kotlin Project Library
 * Copyright (C) 2024 Francisco Solís
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cl.franciscosolis.simplecoreapi.global.modules.translationsmodule.models

import cl.franciscosolis.simplecoreapi.global.modules.filesmodule.config.YmlConfig
import cl.franciscosolis.simplecoreapi.global.modules.filesmodule.extensions.folder
import cl.franciscosolis.simplecoreapi.global.modules.translationsmodule.managers.TranslationManager
import java.io.File

/**
 * Representation of a translation.
 * @param id The id of the translation
 * @param defaultValue The default value of the translation.
 * @param group The group (folder) where to store this translation. Defaults to "common"
 * @param language The language of the translation. (Default to "en")
 * @param mainColor The main color of the translation. (Default to null)
 * @param colors The colors to use in the translation replacing strings. Example (using color '&c'): '**test**' should return '&ctest'. Default to an empty array.
 * @param autoRegister If the translation should be automatically registered. (Default to true) It is recommended to disable if you're going to initialize the same translation multiple times (for example, inside a loop)
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
     * @param language The language of the translation. Set to null to use the default language. Default to null
     * @param placeholders The placeholders to use in the translation replacing strings. Example (using placeholder id 'test' and value 'test_value'): '{test}' should return 'test_value'.
     *                      You can use '{}' or '%%' as placeholder identifiers like '{test}' or '%test%'. Defaults to an empty map.
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
            translation.replace('&', '\u00A7')
        } else {
            translation
        }
    }

    /**
     * Translates this [Translation] to the current language.
     * @return The translated string.
     */
    override fun toString(): String = translate()

    /**
     * Checks if this [Translation] is equals to another [Translation].
     * @param other The other [Translation] to compare.
     * @return True if the [Translation]s are equals, false otherwise.
     */
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

    /**
     * Gets the hash code of this [Translation].
     * @return The hash code of this [Translation].
     */
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