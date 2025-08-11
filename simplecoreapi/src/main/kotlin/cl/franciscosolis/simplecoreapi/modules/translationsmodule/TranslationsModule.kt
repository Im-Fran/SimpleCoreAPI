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

package cl.franciscosolis.simplecoreapi.modules.translationsmodule

import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.module.Module
import cl.franciscosolis.simplecoreapi.module.ModuleDescription
import cl.franciscosolis.simplecoreapi.modules.filesmodule.config.YmlConfig
import cl.franciscosolis.simplecoreapi.modules.translationsmodule.models.Translation
import cl.franciscosolis.simplecoreapi.utils.measureLoad
import cl.franciscosolis.simplecoreapi.utils.text.TextColor
import java.io.File

class TranslationsModule: Module {

    private val translationSettings = YmlConfig(File(SimpleCoreAPI.dataFolder(), "TranslationSettings.yml")).add("language", "en")
    val cachedTranslations = mutableMapOf<String, String>()

    override val description: ModuleDescription = ModuleDescription(
        name = "TranslationsModule",
        version = "0.4.0",
        authors = listOf("Im-Fran")
    )

    override fun onEnable() {
        measureLoad("Loaded translations in {time}") {
            loadTranslations()
        }
    }

    /**
     * Loads all translations into cache from the files.
     */
    fun loadTranslations() {
        val cached = mutableMapOf<String, String>()
        loadTranslationsInFolder(SimpleCoreAPI.dataFolder("Translations/"), cached)

        cachedTranslations.clear()
        cachedTranslations.putAll(cached)
        SimpleCoreAPI.logger.info("${cachedTranslations.count()} translations have been loaded.")
    }

    private fun loadTranslationsInFolder(folder: File, cache: MutableMap<String, String> = mutableMapOf()) {
        for(file in (folder.listFiles() ?: emptyArray())) {
            if(file.isDirectory) {
                loadTranslationsInFolder(folder = file, cache = cache)
                continue
            }

            val lang = file.nameWithoutExtension
            val group = file.absolutePath.substringAfter(SimpleCoreAPI.dataFolder("Translations/").absolutePath).substringBeforeLast("$lang.lang")
            val cfg = YmlConfig(file)
            cfg.keys(deep = true).forEach { id ->
                cache["SimpleCoreAPI/Translations/${if (group.endsWith("/")) group else "$group/"}/$lang/$id"] = cfg.getString(id)
            }
        }
    }

    /**
     * Creates a new translation
     * @param id The id of the translation
     * @param defaultValue The default value of the translation.
     * @param group The group (folder) where to store this translation. Defaults to "common"
     * @param language The language of the translation. (Default to "en")
     * @param mainColor The main color of the translation. (Default to TextColor.WHITE)
     * @param colors The colors to use in the translation replacing strings. Example (using color '&c'): '**test**' should return '&ctest'. Default to an empty array.
     * @param autoRegister If the translation should be automatically registered. (Default to true) It is recommended to disable if you're going to initialize the same translation multiple times (for example, inside a loop)
     * @return The created translation
     */
    fun createTranslation(
        id: String,
        defaultValue: String,
        group: String = "common",
        language: String = "en",
        mainColor: TextColor = TextColor.WHITE,
        colors: Array<TextColor> = emptyArray(),
        autoRegister: Boolean = true
    ): Translation = Translation(id = id, defaultValue = defaultValue, group = group, language = language, mainColor = mainColor, colors = colors, autoRegister = autoRegister)


    /**
     * Gets the current language from the TranslationsSettings.yml file
     * @return The current language. (Defaults to "en")
     */
    fun getCurrentLanguage(): String = translationSettings.getStringOrAdd("language", "en")
}