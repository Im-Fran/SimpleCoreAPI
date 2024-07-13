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