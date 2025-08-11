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

package cl.franciscosolis.simplecoreapi

/**
 * Gets the short version of the commit hash
 * @return The short commit hash
 */
const val getShortHash: String = "{{ git_short }}"

/**
 * Gets the full version of the commit hash
 * @return The full commit hash
 */
const val getFullHash: String = "{{ git_full }}"

/**
 * Gets the version of SimpleCoreAPI
 * @return The version of SimpleCoreAPI
 */
const val getVersion: String = "{{ version }}"

/**
 * Gets the name of the project ('SimpleCoreAPI')
 * @return The name of the project
 */
const val getName: String = "{{ name }}"

/**
 * Gets the description of the project
 * @return The description of the project
 */
const val getDescription: String = "{{ description }}"

/**
 * Gets the versions of the dependencies of the project
 * @return The versions of the dependencies of the project
 */
val getDependencyVersions: Map<String, String> = mapOf(
    "log4j.version" to "{{ log4j_version }}",
    "simpleyaml.version" to "{{ simpleyaml_version }}",
    "jetbrains-annotations.version" to "{{ jetbrains_annotations_version }}",
    "commons-io.version" to "{{ commons_io_version }}",
    "google-gson.version" to "{{ google_gson_version }}",
    "json.version" to "{{ json_version }}",
    "zip4j.version" to "{{ zip4j_version }}",
    "slf4j.version" to "{{ slf4j_version }}",
    "xseries.version" to "{{ xseries_version }}"
)