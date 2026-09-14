package cl.franciscosolis.exampleplugin

import cl.franciscosolis.simplecoreapi.module.ModuleManager
import cl.franciscosolis.simplecoreapi.module.requireModule
import cl.franciscosolis.simplecoreapi.modules.files.FilesModule
import cl.franciscosolis.simplecoreapi.paper.modules.command.CommandModule
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.plugin.java.JavaPlugin

/**
 * Example plugin: verifies end to end that SimpleCoreAPI works as a dependency.
 *
 * This jar contains neither SimpleCoreAPI nor kotlin-stdlib: both are provided by the
 * SimpleCoreAPI plugin, which the server loads first thanks to the dependency declared in
 * `paper-plugin.yml`.
 */
class ExamplePlugin : JavaPlugin() {

    override fun onEnable() {
        // Before this line no module has been instantiated at all.
        logger.info("Modules loaded before asking for anything: ${ModuleManager.loaded.size}")

        val files = requireModule<FilesModule>()
        val config = files.yaml(
            "example/config.yml",
            defaults = mapOf(
                "message" to "Logged in!",
                "usage" to "Usage: /login <password>",
            ),
        )

        val commands = requireModule<CommandModule>()
        commands.registerCommand(
            name = "login",
            aliases = listOf("l"),
            permission = "example.login",
            description = "Example command registered through SimpleCoreAPI",
        ) { sender, args ->
            if (args.isEmpty()) {
                sender.sendPlainMessage(config.getString("usage")!!)
            } else {
                sender.sendPlainMessage(config.getString("message")!!)
            }
        }

        // Paper-only: typed argument tree with native client-side suggestions.
        commands.registerBrigadier { registrar ->
            registrar.register(
                Commands.literal("greet")
                    .then(
                        Commands.argument("name", StringArgumentType.word())
                            .executes { ctx ->
                                val name = StringArgumentType.getString(ctx, "name")
                                ctx.source.sender.sendPlainMessage("Hello, $name")
                                Command.SINGLE_SUCCESS
                            },
                    )
                    .build(),
                "Greets someone",
            )
        }

        logger.info("Modules loaded after registering commands: ${ModuleManager.loaded.map { it.simpleName }}")
    }
}
