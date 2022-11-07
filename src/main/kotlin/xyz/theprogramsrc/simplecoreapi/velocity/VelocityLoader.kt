package xyz.theprogramsrc.simplecoreapi.velocity

import com.google.inject.Inject
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.utils.logger.SLF4JLogger

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
        SimpleCoreAPI(SLF4JLogger(this.logger))
        println("version_vendor: " + this.server.version.vendor)
        println("version_version: " + this.server.version.version)
        println("version_name: " + this.server.version.name)
    }
}