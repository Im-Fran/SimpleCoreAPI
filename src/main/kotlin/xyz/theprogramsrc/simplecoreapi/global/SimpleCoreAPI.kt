package xyz.theprogramsrc.simplecoreapi.global

import xyz.theprogramsrc.simplecoreapi.global.module.ModuleManager
import java.util.logging.Logger

/**
 * Class used to initialize SimpleCoreAPI (DO NOT CALL IT FROM EXTERNAL PLUGINS, IT MAY CRASH)
 * @param logger The logger to use
 */
class SimpleCoreAPI(logger: Logger) {

    companion object {
        /**
         * Instance of SimpleCoreAPI. Use it to retrieve the module manager
         * @return The instance of SimpleCoreAPI
         */
        lateinit var instance: SimpleCoreAPI
    }

    /**
     * The Module Manager
     * @return The {@link ModuleManager}
     */
    val moduleManager: ModuleManager

    init {
        instance = this
        moduleManager = ModuleManager.init(logger)
    }

}