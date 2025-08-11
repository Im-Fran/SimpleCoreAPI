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

package cl.franciscosolis.simplecoreapi.modules.translationsmodule.models

import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.module.requireModule
import cl.franciscosolis.simplecoreapi.modules.filesmodule.config.YmlConfig
import cl.franciscosolis.simplecoreapi.modules.translationsmodule.TranslationsModule
import cl.franciscosolis.simplecoreapi.utils.color.MinecraftColor
import cl.franciscosolis.simplecoreapi.utils.text.Text
import cl.franciscosolis.simplecoreapi.utils.text.TextColor
import java.io.File

/**
 * Representation of a translation.
 * @param id The id of the translation
 * @param defaultValue The default value of the translation.
 * @param group The group (folder) where to store this translation. Defaults to "common"
 * @param language The language of the translation. (Default to "en")
 * @param mainColor The main color of the translation. (Defaults to TextColor.WHITE)
 * @param colors The colors to use in the translation replacing strings. Example (using color '&c'): '**test**' should return '&ctest'. Default to an empty array.
 * @param autoRegister If the translation should be automatically registered. (Default to true) It is recommended to disable if you're going to initialize the same translation multiple times (for example, inside a loop)
 */
data class Translation(
    val id: String,
    val defaultValue: String,
    val group: String = "common",
    val language: String = "en",
    val mainColor: TextColor = TextColor.WHITE,
    val colors: Array<TextColor> = emptyArray(),
    val autoRegister: Boolean = true
) {

    init {
        if(autoRegister) {
            YmlConfig(File(SimpleCoreAPI.dataFolder("Translations/${if (group.endsWith("/")) group else "$group/"}"), "$language.lang"))
                .add(id, defaultValue)
                .save()
        }
    }

    /**
     * Translates this [Translation] to the current language.
     * @param language The language of the translation. Set to null to use the default language. Default to null
     * @return The translated string.
     */
    fun translate(language: String? = null, colorize: Boolean = true): String {
        val translationsModule = requireModule<TranslationsModule>()
        val translation = translationsModule.cachedTranslations.computeIfAbsent("SimpleCoreAPI/Translations/${if (group.endsWith("/")) group else "$group/"}/${language ?: translationsModule.getCurrentLanguage()}/$id") {
            val file = YmlConfig(File(SimpleCoreAPI.dataFolder("Translations/${if (group.endsWith("/")) group else "$group/"}"), (language ?: translationsModule.getCurrentLanguage()) + ".lang"))
            if(autoRegister) {
                file.getStringOrAdd(path = id, default = defaultValue)
            } else {
                if(file.has(path = id)) file.getString(path = id) else defaultValue
            }
        }


        return Text(content = translation, colors = colors, mainColor = mainColor).let {
            if(colorize) it.colorize() else it
        }.toString()
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