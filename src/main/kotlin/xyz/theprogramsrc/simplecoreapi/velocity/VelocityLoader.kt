package xyz.theprogramsrc.simplecoreapi.velocity

import com.google.inject.Inject
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.utils.logger.JavaLogger
import java.util.logging.Logger

@Plugin(
    id = "simplecoreapi",
    name = "SimpleCoreAPI",
    version = "@version@",
    url = "theprogramsrc.xyz",
    authors = ["TheProgramSrc"]
)
class VelocityLoader @Inject constructor(val server: ProxyServer, val logger: Logger) {

    companion object {
        lateinit var instance: VelocityLoader
            private set
    }

    init {
        instance = this
        SimpleCoreAPI(JavaLogger(this.logger))
    }
}