package cl.franciscosolis.simplecoreapi

/**
 * Platform SimpleCoreAPI is currently running on.
 *
 * Each platform bootstrap sets it on startup. In a JVM project that uses the core as a
 * plain library, with no Minecraft server around, the value is [STANDALONE].
 */
public enum class Platform {
    PAPER,
    SPIGOT,
    BUNGEE,
    STANDALONE;

    /** `true` on any Minecraft platform, `false` on [STANDALONE]. */
    public val isMinecraft: Boolean get() = this != STANDALONE

    /** `true` on Paper and Spigot, where the Bukkit API is available. */
    public val isBukkit: Boolean get() = this == PAPER || this == SPIGOT
}
