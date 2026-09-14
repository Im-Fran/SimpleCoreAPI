package cl.franciscosolis.simplecoreapi

import java.io.File
import java.util.logging.Logger

/**
 * Global context of SimpleCoreAPI.
 *
 * Each platform bootstrap fills it in on `onEnable`. The defaults let you use the core
 * outside a Minecraft server without any initialization at all.
 */
public object SimpleCoreAPI {

    /** Current platform. */
    public var platform: Platform = Platform.STANDALONE
        private set

    /** The platform logger, or a standalone one when there is no server. */
    public var logger: Logger = Logger.getLogger("SimpleCoreAPI")
        private set

    /**
     * Folder where modules store their data.
     *
     * On a server it points to `plugins/SimpleCoreAPI`; standalone, to `simplecoreapi`
     * inside the working directory.
     */
    public var dataFolder: File = File(System.getProperty("user.dir"), "simplecoreapi")
        private set

    /** Called by each platform bootstrap. Not meant to be called from a plugin. */
    public fun init(platform: Platform, logger: Logger, dataFolder: File) {
        this.platform = platform
        this.logger = logger
        this.dataFolder = dataFolder.also { it.mkdirs() }
        logger.info("SimpleCoreAPI started on ${platform.name.lowercase()}")
    }

    /** Restores the default context. Meant for tests and for plugin shutdown. */
    public fun reset() {
        platform = Platform.STANDALONE
        logger = Logger.getLogger("SimpleCoreAPI")
        dataFolder = File(System.getProperty("user.dir"), "simplecoreapi")
    }
}
