<div align="center">

# 📄 SimpleCoreAPI — Paper

**Paper bootstrap and modules for SimpleCoreAPI, including runtime command registration and Brigadier access.**

[![License](https://img.shields.io/badge/license-GPL--3.0-blue)](../LICENSE)
[![CI](https://img.shields.io/github/actions/workflow/status/Im-Fran/SimpleCoreAPI/ci.yml?label=CI)](https://github.com/Im-Fran/SimpleCoreAPI/actions/workflows/ci.yml)
[![Paper](https://img.shields.io/badge/paper-26.2-blue)](https://papermc.io)

</div>

---

## 📖 Overview

`paper` is published as **`simplecoreapi-paper`**. It depends on `core` through `api`, so
the module registry, `FilesModule`, `CompressionModule` and `NetworkingModule` all come along (see
[`core`](../core)). The Paper API itself is `compileOnly`, because the server provides it.

It adds:

- **`PaperBootstrap`**: the `JavaPlugin` that `paper-plugin.yml` points to in the universal jar.
- **`CommandModule`**: registers commands at runtime, plus direct Brigadier access.

---

## 📦 Installation

```kotlin
dependencies {
    compileOnly("cl.franciscosolis:simplecoreapi-paper:0.1.0")
}
```

Use `compileOnly`: SimpleCoreAPI is already installed on the server as a plugin. Then declare it in
your `paper-plugin.yml`:

```yaml
dependencies:
  server:
    SimpleCoreAPI:
      load: BEFORE
      required: true
      join-classpath: true
```

`join-classpath: true` gives your plugin access to SimpleCoreAPI's classes, including
`kotlin-stdlib`.

---

## 🚀 `PaperBootstrap`

| Phase | What it does |
|---|---|
| `onEnable` | `SimpleCoreAPI.init(Platform.PAPER, logger, dataFolder)` and installs the Brigadier `COMMANDS` lifecycle hook |
| `onDisable` | `ModuleManager.disableAll()`, then `SimpleCoreAPI.reset()` |

It loads no modules. It installs the Brigadier hook at startup because Paper only accepts lifecycle
handlers while the owning plugin is enabling, and `CommandModule` is usually requested later. The
hook itself instantiates nothing.

`PaperBootstrap.instance` gives access to the plugin instance once the server has enabled it.

---

## ⌨️ `CommandModule`

### Simple commands: any time

Same signature as on Spigot and BungeeCord. The command goes into the server's `CommandMap` under
the `simplecoreapi:` fallback prefix, and you don't need to declare it in your descriptor.

```kotlin
val commands = requireModule<CommandModule>()

commands.registerCommand(
    name = "login",
    aliases = listOf("l"),
    permission = "simpleauth.login",   // checked by Bukkit before the executor runs
    description = "Log in",
    usage = "/login <password>",
    tabCompleter = { sender, args -> listOf("help") },
) { sender, args ->
    sender.sendPlainMessage("Usage: /login <password>")
}

commands.unregisterCommand("login")
commands.commands   // names registered through registerCommand
```

- Registering a name that already exists replaces the previous command.
- After each register or unregister, online players get their command list refreshed
  (`updateCommands()`), so tab completion stays accurate.
- Unregistering really removes the entry from `knownCommands`, so the old command is no longer
  dispatched.

### Brigadier: during `onEnable`

```kotlin
commands.registerBrigadier { registrar ->
    registrar.register(
        Commands.literal("greet")
            .then(
                Commands.argument("name", StringArgumentType.word())
                    .executes { ctx ->
                        ctx.source.sender.sendPlainMessage("Hello, ${StringArgumentType.getString(ctx, "name")}")
                        Command.SINGLE_SUCCESS
                    },
            )
            .build(),
        "Greets someone",
    )
}
```

The builder runs again on every `COMMANDS` event, so these commands survive datapack reloads. Paper
fires that event after plugins are enabled, so **call `registerBrigadier` from your plugin's
`onEnable`**. If you register later, the command won't appear until the next datapack reload. For
commands registered late, use `registerCommand`.

On shutdown, the module unregisters every command it added and clears the Brigadier builders.

---

## 🔨 Build

```bash
./gradlew :paper:build
```

This module produces only the library artifact. The installable plugin jar comes from
`./gradlew :dist:shadowJar`.

---

## 📄 License

**GNU General Public License v3.0**. See [LICENSE](../LICENSE).

---

<div align="center">
Made with ☕ by <a href="https://franciscosolis.cl">Fran</a>
</div>
