<div align="center">

# 🧩 SimpleCoreAPI — Core

**The platform-independent heart of SimpleCoreAPI: the lazy module registry and the modules that need no Minecraft server.**

[![License](https://img.shields.io/badge/license-GPL--3.0-blue)](../LICENSE)
[![CI](https://img.shields.io/github/actions/workflow/status/Im-Fran/SimpleCoreAPI/ci.yml?label=CI)](https://github.com/Im-Fran/SimpleCoreAPI/actions/workflows/ci.yml)

</div>

---

## 📖 Overview

`core` is published as **`simplecoreapi-core`**. It has no Minecraft dependencies, so it works in
any JVM project as well as underneath the [`paper`](../paper), [`spigot`](../spigot) and
[`bungee`](../bungee) modules, which all expose it through `api(project(":core"))`.

It contains three things:

- **`SimpleCoreAPI`**: the global context (platform, logger, data folder).
- **`ModuleManager`**: the lazy, reentrant module registry, plus the `requireModule<T>()` shortcut.
- **The bundled modules**: `FilesModule`, `CompressionModule` and `NetworkingModule`.

Its only external dependencies are `snakeyaml` and `gson`. Neither appears in the public API, and
the universal jar relocates both under `cl.franciscosolis.simplecoreapi.libs`.

---

## 📦 Installation

```kotlin
dependencies {
    // Standalone JVM project: ship it with your application.
    implementation("cl.franciscosolis:simplecoreapi-core:0.1.0")
}
```

On a Minecraft server, depend on the platform artifact (`simplecoreapi-paper`, `-spigot` or
`-bungee`) with `compileOnly` instead. Each one already brings `core` along.

---

## 🌍 `SimpleCoreAPI` and `Platform`

```kotlin
SimpleCoreAPI.platform      // PAPER, SPIGOT, BUNGEE or STANDALONE
SimpleCoreAPI.logger        // the platform logger, or a plain java.util.logging one
SimpleCoreAPI.dataFolder    // plugins/SimpleCoreAPI on a server, ./simplecoreapi standalone

SimpleCoreAPI.platform.isMinecraft  // false only on STANDALONE
SimpleCoreAPI.platform.isBukkit     // true on PAPER and SPIGOT
```

Each platform bootstrap calls `SimpleCoreAPI.init(...)` on enable and `reset()` on disable. When
there's no server, nothing needs initializing and the defaults apply.

---

## ⚙️ Module system

```kotlin
val files = requireModule<FilesModule>()   // created on first call, shared afterwards
ModuleManager.isLoaded(FilesModule::class.java)   // true
ModuleManager.loaded                               // loaded classes, in load order
```

| Behavior | Details |
|---|---|
| Lazy | Nothing is instantiated at startup. The first `requireModule<T>()` creates the instance. |
| Shared | Every later call, from any plugin, returns the same instance. |
| Reentrant | A module can call `requireModule` from its constructor or `onEnable`. |
| Cycle detection | `A -> B -> A` fails with an `IllegalStateException` that shows the whole chain. |
| Clear errors | A missing no-arg constructor or a failing constructor throws the real cause, not a reflection wrapper. |
| Ordered shutdown | `ModuleManager.disableAll()` calls `onDisable` in reverse load order and logs failures without stopping the rest. |

A module is any class that implements `Module` and has a public no-argument constructor:

```kotlin
class EconomyModule : Module {
    private val files = requireModule<FilesModule>()
    private lateinit var data: YamlFile

    override fun onEnable() { data = files.yaml("economy.yml") }
    override fun onDisable() { data.save() }
}
```

---

## 📦 Bundled modules

### `FilesModule`

YAML and JSON files inside `SimpleCoreAPI.dataFolder`. Both go through `DataFile`, which reads and
writes values with dot-separated paths.

```kotlin
val files = requireModule<FilesModule>()
val config = files.yaml("config.yml", defaults = mapOf("locale" to "en", "limits.players" to 20))

config.getString("locale")          // "en"
config.getInt("limits.players")     // 20
config["limits.players"] = 50
config.save()

val data = files.json("data.json")
```

- Files are **cached by path**, so plugins that open the same file share one instance.
  `clearCache()` makes the next access read from disk again.
- Defaults only **fill in missing keys** and never overwrite values the user has already set.
- Changes stay in memory until you call `save()`, and `reload()` reads the file again.
- Typed getters: `getString`, `getInt`, `getLong`, `getDouble`, `getBoolean`, `getList`,
  `getStringList`. Also available: `contains`, `remove`, `keys`, `section`, `add`, `applyDefaults`.
- JSON numbers come back as `Double` (that's how gson returns them), so use `getInt`/`getLong`
  instead of casting.
- The dot is always a separator. To reach a key that contains dots, use `section(...)`.

### `CompressionModule`

Built only on `java.util.zip`.

```kotlin
val compression = requireModule<CompressionModule>()

compression.zip(File("plugins/MyPlugin"), File("backups/myplugin.zip"))   // file or folder
compression.unzip(File("backups/myplugin.zip"), File("restore"))          // zip slip protected
compression.gzip(File("latest.log"), File("latest.log.gz"))
compression.gunzip(File("latest.log.gz"), File("latest.log"))

val packed = compression.gzip(bytes)       // ByteArray overloads
compression.gzip(inputStream, outputStream) // stream overload, leaves both open
```

### `NetworkingModule`

HTTP on top of the JDK `HttpClient`: 15 s connect timeout, follows redirects, no extra
dependencies.

```kotlin
val net = requireModule<NetworkingModule>()

val response = net.get("https://api.example.com/version", headers = mapOf("Accept" to "text/plain"))
if (response.isSuccess) println(response.body)   // code, body, headers

net.post(url, body = "a=1")
net.postJson(url, body = """{"a":1}""")
net.download("https://example.com/file.zip", File("downloads/file.zip"))
```

Every method takes an optional `headers` map and `timeout`. `download` throws when the response
isn't 2xx and writes nothing in that case. **All calls block**, so on a server run them off the
main thread.

---

## 🧪 Tests

The core is tested without a Minecraft server:

```bash
./gradlew :core:test
```

The tests cover the registry (laziness, reentrancy, cycles, shutdown order), `DataFile`,
compression and networking.

---

## 📄 License

**GNU General Public License v3.0**. See [LICENSE](../LICENSE).

---

<div align="center">
Made with ☕ by <a href="https://franciscosolis.cl">Fran</a>
</div>
