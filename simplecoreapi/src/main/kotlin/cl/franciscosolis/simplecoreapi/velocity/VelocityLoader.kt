package cl.franciscosolis.simplecoreapi.velocity

import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI
import com.google.inject.Inject
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import java.util.logging.Logger

@Plugin(
    id = "simplecoreapi",
    name = "SimpleCoreAPI",
    version = cl.franciscosolis.simplecoreapi.getVersion,
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
        SimpleCoreAPI()
    }
}