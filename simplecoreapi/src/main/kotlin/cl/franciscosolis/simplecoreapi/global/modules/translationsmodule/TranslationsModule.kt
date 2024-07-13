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

package cl.franciscosolis.simplecoreapi.global.modules.translationsmodule

import cl.franciscosolis.simplecoreapi.global.module.Module
import cl.franciscosolis.simplecoreapi.global.module.ModuleDescription
import cl.franciscosolis.simplecoreapi.global.modules.translationsmodule.managers.TranslationManager
import cl.franciscosolis.simplecoreapi.global.utils.measureLoad

class TranslationsModule: Module {
    override val description: ModuleDescription = ModuleDescription(
        name = "TranslationsModule",
        version = "0.4.0",
        authors = listOf("Im-Fran")
    )

    override fun onEnable() {
        measureLoad("'TranslationManager' loaded in {time}"){
            TranslationManager()
        }
    }

    override fun onDisable() {
    }
}