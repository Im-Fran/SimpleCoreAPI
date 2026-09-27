<div align="center">

# 🌉 SimpleCoreAPI — BungeeCord

**BungeeCord bootstrap and modules for SimpleCoreAPI, so proxy plugins can use the same module system as the backend servers.**

[![License](https://img.shields.io/badge/license-GPL--3.0-blue)](../LICENSE)
[![CI](https://img.shields.io/github/actions/workflow/status/Im-Fran/SimpleCoreAPI/ci.yml?label=CI)](https://github.com/Im-Fran/SimpleCoreAPI/actions/workflows/ci.yml)
[![BungeeCord](https://img.shields.io/badge/bungeecord-26.1-yellow)](https://www.spigotmc.org/wiki/bungeecord/)

</div>

---

## 📖 Overview

`bungee` is published as **`simplecoreapi-bungee`**. It depends on `core` through `api`, so
the module registry, `FilesModule`, `CompressionModule` and `NetworkingModule` all come along (see
[`core`](../core)). The BungeeCord API itself is `compileOnly`, because the proxy provides it.

It adds:

- **`BungeeBootstrap`**: the BungeeCord `Plugin` that `bungee.yml` points to in the universal jar.
- **`CommandModule`**: registers proxy commands at runtime.

---

## 📦 Installation

```kotlin
dependencies {
    compileOnly("cl.franciscosolis:simplecoreapi-bungee:0.1.0")
}
```

Use `compileOnly`: SimpleCoreAPI is already installed on the proxy as a plugin. Then declare it in
your `bungee.yml`:

```yaml
depends: [ SimpleCoreAPI ]
```

---

## 🚀 `BungeeBootstrap`

| Phase | What it does |
|---|---|
| `onEnable` | `SimpleCoreAPI.init(Platform.BUNGEE, logger, dataFolder)` |
| `onDisable` | `ModuleManager.disableAll()`, then `SimpleCoreAPI.reset()` |

It loads no modules: each one is created the first time a plugin requests it.
`BungeeBootstrap.instance` gives access to the plugin instance once the proxy has enabled it.

---

## ⌨️ `CommandModule`

Registers proxy commands at any time. The call looks the same as on Paper and Spigot, except there
are no `description` or `usage` parameters, because BungeeCord commands don't have them.

```kotlin
val commands = requireModule<CommandModule>()

commands.registerCommand(
    name = "lobby",
    aliases = listOf("hub"),
    permission = "network.lobby",   // checked by BungeeCord before the executor runs
    tabCompleter = { sender, args -> emptyList() },
) { sender, args ->
    sender.sendMessage(TextComponent("Sending you to the lobby..."))
}

commands.unregisterCommand("lobby")
commands.commands   // names registered by this module
```

- Commands are registered with BungeeCord's `PluginManager` under the SimpleCoreAPI plugin, not
  under your own plugin.
- When a `tabCompleter` is passed, it's plugged in through BungeeCord's `TabExecutor`.
- Registering a name that already exists replaces the previous command.
- On shutdown, the module unregisters every command it added.

---

## 🔨 Build

```bash
./gradlew :bungee:build
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
