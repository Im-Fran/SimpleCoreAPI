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

package cl.franciscosolis.simplecoreapi.global.modules.translationsmodule.managers

import cl.franciscosolis.simplecoreapi.global.modules.filesmodule.config.YmlConfig
import cl.franciscosolis.simplecoreapi.global.modules.filesmodule.extensions.folder
import cl.franciscosolis.simplecoreapi.global.modules.translationsmodule.models.Translation
import java.io.File

class TranslationManager {

    private val translationsCache = mutableMapOf<String, MutableList<Translation>>()
    private val cache = mutableMapOf<String, MutableMap<String, MutableMap<String, String>>>()

    companion object {
        private val translationSettings = YmlConfig(File(File("plugins/SimpleCoreAPI").folder(), "TranslationSettings.yml")).add("language", "en")
        lateinit var instance: TranslationManager
            private set

        fun getCurrentLanguage(): String = translationSettings.getStringOrAdd("language", "en")
    }

    init {
        instance = this
        loadTranslations()
    }

    /**
     * Register the given translations to the given group id
     * @param group The group (folder) of the translation. Defaults to "common"
     * @param translation The translations to register
     */
    fun registerTranslation(group: String = "common", translation: Translation) = this.registerTranslations(group, listOf(translation))

    /**
     * Register the given translations to the given group id
     * @param group The group (folder) of the translation. Defaults to "common"
     * @param translations The translations to register
     */
    fun registerTranslations(group: String = "common", vararg translations: Translation) = this.registerTranslations(group, translations.toList())

    /**
     * Register the given translations to the given group id
     * @param group The group (folder) of the translation. Defaults to "common"
     * @param translations The translations to register
     */
    fun registerTranslations(group: String = "common", translations: Collection<Translation>) {
        val translationsCache = this.translationsCache[group] ?: mutableListOf()
        translations.forEach { t ->
            if (translationsCache.find { it == t } == null) {
                translationsCache.add(t)
            }
        }
        this.translationsCache[group] = translationsCache
        loadTranslations()
    }

    /**
     * Get the translation for the given id in the given language
     * @param id The id of the translation
     * @param language The language of the translation. Set to null to use the default language. Defaults to null
     * @param group The group (folder) of the translation. Defaults to "common"
     * @param placeholders The placeholders to replace in the translation. Defaults to empty map
     * @return The translation. If the translation is not found, the id is returned
     */
    fun translate(id: String, language: String? = null, group: String = "common", placeholders: Map<String, String> = emptyMap()): String {
        val cached = cache[group] ?: return id
        var translation = ((cached[language] ?: cached[getCurrentLanguage()] ?: return id)[id] ?: return id)
        placeholders.forEach { (key, value) ->
            translation = translation.replace("{$key}", value).replace("%$key%", value)
        }
        return translation
    }

    /**
     * Loads the translations to the cache.
     */
    fun loadTranslations() {
        val translationsFolder = File("SimpleCoreAPI/translations/").folder()
        // First load the default translations
        translationsCache.forEach { (group, translations) ->
            val folder = File(translationsFolder, group).folder()
            translations.forEach {
                val languageFile = YmlConfig(File(folder, "${it.language}.lang"))
                languageFile.add(it.id, it.defaultValue)
            }
        }

        // Then load the translations from the language files
        (translationsFolder.listFiles() ?: emptyArray()).filter(File::isDirectory).forEach { groupFolder ->
            val cached = cache[groupFolder.name] ?: mutableMapOf()
            (groupFolder.listFiles() ?: emptyArray()).filter { it.extension == "lang" }.forEach {
                val langCache = cached[it.nameWithoutExtension] ?: mutableMapOf()
                val cfg = YmlConfig(it)
                cfg.keys(true).forEach { id ->
                    val t = translationsCache[groupFolder.name]?.find { t1 -> t1.id == id }
                    if (t != null) {
                        langCache[id] = t.translate(it.nameWithoutExtension)
                    }
                }
                if (langCache.isNotEmpty()) {
                    cached[it.nameWithoutExtension] = langCache
                }
            }

            if (cached.isNotEmpty()) {
                cache[groupFolder.name] = cached
            }
        }
    }

    /**
     * Count the amount of translations
     * @param group The group (folder) of the translation. If null it'll count all the groups. Defaults to null
     * @param lang The language of the translation. If null it'll count all the languages. Defaults to null
     */
    fun countTranslations(group: String? = null, lang: String? = null): Int {
        return if (group == null) {
            cache.values.sumOf {
                if (lang != null) {
                    it[lang]?.size ?: 0
                } else {
                    it.values.sumOf { it2 ->
                        it2.values.size
                    }
                }
            }
        } else {
            val c = (this.cache[group] ?: return -2)
            if (lang != null) {
                c[lang]?.size ?: 0
            } else {
                c.values.sumOf { it2 ->
                    it2.values.size
                }
            }
        }
    }
}