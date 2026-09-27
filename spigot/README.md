<div align="center">

# 🚰 SimpleCoreAPI — Spigot

**Spigot bootstrap and modules for SimpleCoreAPI, including runtime command registration without touching `plugin.yml`.**

[![License](https://img.shields.io/badge/license-GPL--3.0-blue)](../LICENSE)
[![CI](https://img.shields.io/github/actions/workflow/status/Im-Fran/SimpleCoreAPI/ci.yml?label=CI)](https://github.com/Im-Fran/SimpleCoreAPI/actions/workflows/ci.yml)
[![Spigot](https://img.shields.io/badge/spigot-26.2-orange)](https://www.spigotmc.org)

</div>

---

## 📖 Overview

`spigot` is published as **`simplecoreapi-spigot`**. It depends on `core` through `api`, so
the module registry, `FilesModule`, `CompressionModule` and `NetworkingModule` all come along (see
[`core`](../core)). The Spigot API itself is `compileOnly`, because the server provides it.

It adds:

- **`SpigotBootstrap`**: the `JavaPlugin` that `plugin.yml` points to in the universal jar.
- **`CommandModule`**: registers commands at runtime.

---

## 📦 Installation

```kotlin
dependencies {
    compileOnly("cl.franciscosolis:simplecoreapi-spigot:0.1.0")
}
```

Use `compileOnly`: SimpleCoreAPI is already installed on the server as a plugin. Then declare it in
your `plugin.yml`:

```yaml
depend: [ SimpleCoreAPI ]
```

---

## 🚀 `SpigotBootstrap`

| Phase | What it does |
|---|---|
| `onEnable` | `SimpleCoreAPI.init(Platform.SPIGOT, logger, dataFolder)` |
| `onDisable` | `ModuleManager.disableAll()`, then `SimpleCoreAPI.reset()` |

It loads no modules: each one is created the first time a plugin requests it.
`SpigotBootstrap.instance` gives access to the plugin instance once the server has enabled it.

---

## ⌨️ `CommandModule`

Registers commands at any time, including long after startup, without declaring them in your
`plugin.yml`. Same signature as on Paper and BungeeCord.

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
    sender.sendMessage("Usage: /login <password>")
}

commands.unregisterCommand("login")
commands.commands   // names registered by this module
```

- Commands go into the server's `CommandMap` under the `simplecoreapi:` fallback prefix.
- Registering a name that already exists replaces the previous command.
- After each change, online players get their command list refreshed (`updateCommands()`).
- On shutdown, the module unregisters every command it added.

### How it reaches the `CommandMap`

Spigot doesn't expose the `CommandMap` in its API, so the module uses reflection twice:
`CraftServer#getCommandMap()`, which is public on every version, and the protected
`SimpleCommandMap.knownCommands` field. Removing the entry from that field is what really frees the
command name once it's unregistered. If the field can't be accessed, unregistering still works
through `Command#unregister`, but the name stays taken.

---

## 🔨 Build

```bash
./gradlew :spigot:build
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
