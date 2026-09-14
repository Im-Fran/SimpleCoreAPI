<div align="center">

# 🧩 SimpleCoreAPI

**A modular Kotlin API for building Minecraft plugins on Paper, Spigot and BungeeCord — without shipping the same library inside every plugin.**

[![License](https://img.shields.io/badge/license-GPL--3.0-blue)](LICENSE)
[![CI](https://img.shields.io/github/actions/workflow/status/Im-Fran/SimpleCoreAPI/ci.yml?label=CI)](https://github.com/Im-Fran/SimpleCoreAPI/actions/workflows/ci.yml)
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.20-7F52FF)](https://kotlinlang.org)
[![Java](https://img.shields.io/badge/java-25-ED8B00)](https://adoptium.net)

</div>

---

## 📖 Overview

SimpleCoreAPI is installed on the server as **just another plugin**. Your plugins declare it as a
dependency in their descriptor and compile against it with `compileOnly`: every plugin jar stays
tiny, and `kotlin-stdlib` ships once, inside SimpleCoreAPI. The example plugin in this repository
weighs **4.7 KB** and contains neither SimpleCoreAPI nor the Kotlin standard library.

Everything it offers is organized into **modules loaded on demand**. When the server starts there is
not a single instance in memory: the first call to `requireModule<T>()` creates it, and from then on
every plugin shares the same one. A server that only touches files never instantiates the networking
module.

The core (`simplecoreapi-core`) has no Minecraft dependencies and works in any JVM project: its 29
tests run without starting a server.

```kotlin
val commands = requireModule<CommandModule>()

commands.registerCommand("login", aliases = listOf("l"), permission = "simpleauth.login") { sender, args ->
    sender.sendPlainMessage("Usage: /login <password>")
}
```

---

## ✨ Features

- **Genuinely lazy loading** — No module is instantiated until someone asks for it. The registry is
  reentrant, so a module can request others from its own constructor.
- **Circular dependency detection** — `A → B → A` is reported with the full chain instead of blowing
  the stack.
- **One jar for all three platforms** — Paper reads `paper-plugin.yml`, Spigot reads `plugin.yml`
  and BungeeCord reads `bungee.yml`; each one loads only its own bootstrap.
- **Third-party modules with zero setup** — Any class implementing `Module` with a no-argument
  constructor works, no matter which jar it comes from. Your plugins can expose modules to each
  other.
- **`FilesModule`** — YAML and JSON configuration with dot-separated paths, per-file caching and
  defaults that never overwrite what the user customized.
- **`CompressionModule`** — Zip, unzip and gzip on top of `java.util.zip`, with zip slip protection.
- **`NetworkingModule`** — HTTP and downloads on the JDK `HttpClient`, no dependencies.
- **`CommandModule`** — Register commands at runtime without declaring them in your descriptor. Same
  signature on all three platforms, plus Brigadier access on Paper.
- **Relocated dependencies** — `snakeyaml` and `gson` ship under
  `cl.franciscosolis.simplecoreapi.libs` so they never clash with the server's own copies.

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.4.20, Java 25 toolchain |
| Build | Gradle 9.7.1 (multi-project, version catalog) |
| Server | Paper API 26.2, Spigot-API 26.2-R0.1, BungeeCord API 26.1-R0.1 |
| Serialization | snakeyaml 2.7, gson 2.14.0 (relocated) |
| Tests | JUnit 6.1.3 + kotlin-test |
| Packaging | Shadow 9.6.1 |
| Publishing | Maven Central via `com.vanniktech.maven.publish` |
| CI/CD | GitHub Actions |

---

## 📋 Requirements

To **run** SimpleCoreAPI on your server:

- **Java 25** or newer
- **Paper 26.2+**, **Spigot 26.2+** or a recent **BungeeCord**

To **build** this repository:

- **Git**
- The bundled Gradle wrapper — **JDK 25** is downloaded automatically on the first build

---

## 🚀 Getting Started

### Install it on the server

Download `SimpleCoreAPI-<version>.jar` from the
[releases](https://github.com/Im-Fran/SimpleCoreAPI/releases) and drop it into `plugins/`. The same
jar works on all three platforms.

### Use it from your plugin

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    // Pick the artifact for your platform. Each one brings simplecoreapi-core along.
    compileOnly("cl.franciscosolis:simplecoreapi-paper:0.1.0")
    // compileOnly("cl.franciscosolis:simplecoreapi-spigot:0.1.0")
    // compileOnly("cl.franciscosolis:simplecoreapi-bungee:0.1.0")
}
```

`compileOnly` is deliberate: SimpleCoreAPI is already on the server and your jar must not contain it.
Development builds are published under the `cl.franciscosolis.dev` group.

Declare the dependency in your descriptor so the server loads SimpleCoreAPI first:

<table>
<tr><th>Paper — <code>paper-plugin.yml</code></th><th>Spigot — <code>plugin.yml</code></th><th>Bungee — <code>bungee.yml</code></th></tr>
<tr valign="top"><td>

```yaml
dependencies:
  server:
    SimpleCoreAPI:
      load: BEFORE
      required: true
      join-classpath: true
```

</td><td>

```yaml
depend: [ SimpleCoreAPI ]
```

</td><td>

```yaml
depends: [ SimpleCoreAPI ]
```

</td></tr>
</table>

> `join-classpath: true` is what gives your plugin access to SimpleCoreAPI's classes, including
> `kotlin-stdlib`.

### Outside Minecraft

```kotlin
dependencies {
    implementation("cl.franciscosolis:simplecoreapi-core:0.1.0")
}
```

With no server around, `SimpleCoreAPI.platform` is `STANDALONE` and `dataFolder` points at
`simplecoreapi/` inside the working directory. Nothing needs to be initialized.

### Build the repository

```bash
git clone https://github.com/Im-Fran/SimpleCoreAPI.git
cd SimpleCoreAPI
./gradlew build
```

---

## 🧩 Module system

```kotlin
class SimpleAuth : JavaPlugin() {

    override fun onEnable() {
        val files = requireModule<FilesModule>()          // instantiated now
        val config = files.yaml("config.yml", mapOf("attempts" to 3))

        val commands = requireModule<CommandModule>()     // instantiated now
        val sameFiles = requireModule<FilesModule>()      // returns the previous instance
    }
}
```

### Writing your own module

All it takes is implementing `Module` and having a public no-argument constructor. There are no
annotations, no registration step and no classpath scanning, and it works the same when the module
lives inside your own plugin jar: other plugins can consume it with `requireModule<YourModule>()`.

```kotlin
class EconomyModule : Module {

    private val files = requireModule<FilesModule>()   // requesting other modules here is fine
    private lateinit var data: YamlFile

    override fun onEnable() {
        data = files.yaml("economy.yml")
    }

    override fun onDisable() {
        data.save()
    }

    fun balance(uuid: UUID): Double = data.getDouble("balances.$uuid")
}
```

`onEnable` runs exactly once, right after instantiation. `onDisable` runs on server shutdown in
reverse load order, so a module is always disabled before the ones it depends on.

> **Known limitation.** The registry is global and lives as long as SimpleCoreAPI is enabled. If you
> reload a third-party plugin with `/reload`, its module stays around pointing at the old
> classloader. `/reload` is already discouraged and broken on Paper and Spigot: restart the server.

---

## 📦 Bundled modules

### `FilesModule` — core

```kotlin
val files = requireModule<FilesModule>()
val config = files.yaml("config.yml", defaults = mapOf("locale" to "en", "debug" to false))

config.getString("locale")            // "en"
config.getInt("limits.players", 20)   // default value when the path does not exist
config["limits.players"] = 50
config.save()
```

Files are cached by path, so two plugins opening the same configuration see each other's changes.
`JsonFile` works the same way (`files.json("data.json")`).

### `CompressionModule` — core

```kotlin
val compression = requireModule<CompressionModule>()
compression.zip(File("plugins/MyPlugin"), File("backups/myplugin.zip"))
compression.unzip(File("backups/myplugin.zip"), File("plugins/MyPlugin"))
compression.gzip(File("logs/latest.log"), File("logs/latest.log.gz"))
```

`unzip` rejects entries pointing outside the destination folder (zip slip).

### `NetworkingModule` — core

```kotlin
val net = requireModule<NetworkingModule>()

val response = net.get("https://api.example.com/version")
if (response.isSuccess) println(response.body)

net.download("https://example.com/file.zip", File("downloads/file.zip"))
```

Calls are blocking: run them off the server's main thread.

### `CommandModule` — Paper, Spigot and BungeeCord

Registers commands without declaring them in your descriptor, at any time and not just during
startup. The signature is the same on all three platforms; the `sender` is always the native type of
each one, with no conversion layers in between.

```kotlin
val commands = requireModule<CommandModule>()

commands.registerCommand(
    name = "login",
    aliases = listOf("l"),
    permission = "simpleauth.login",
    tabCompleter = { sender, args -> listOf("help") },
) { sender, args ->
    sender.sendPlainMessage("Usage: /login <password>")
}

commands.unregisterCommand("login")
```

On **Paper** you also get direct Brigadier access, with typed arguments and native client-side
suggestions:

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

Call `registerBrigadier` from your plugin's `onEnable`: Paper fires the registration event after
plugins are enabled. If you call it later, the commands will not show up until the next datapack
reload; use `registerCommand` for those cases.

---

## 🏗 Layout and build

| Module | Published artifact | Contents |
|---|---|---|
| `core` | `simplecoreapi-core` | Module registry and the modules with no Minecraft dependencies |
| `paper` | `simplecoreapi-paper` | Paper 26.2 bootstrap and modules |
| `spigot` | `simplecoreapi-spigot` | Spigot 26.2 bootstrap and modules |
| `bungee` | `simplecoreapi-bungee` | BungeeCord bootstrap and modules |
| `dist` | — | Universal jar with all three platforms and `kotlin-stdlib` |
| `example-plugin` | — | Example plugin, end-to-end verification |

```bash
./gradlew build              # compiles the 6 subprojects and runs the tests
./gradlew :core:test         # core tests only, no Minecraft server involved
./gradlew :dist:shadowJar    # universal jar in dist/build/libs/
./gradlew publishToMavenLocal
```

---

## 🌐 Publishing

A `vX.Y.Z` tag triggers the `publish.yml` workflow: it publishes to Maven Central under
`cl.franciscosolis` — the release sits in *staging* for manual approval at `central.sonatype.com` —
and attaches the universal jar to the GitHub release. A `vX.Y.Z-SNAPSHOT` tag publishes under
`cl.franciscosolis.dev`.

Required repository secrets:

| Secret | Description |
|--------|-------------|
| `MAVEN_CENTRAL_USERNAME` | Central Portal user token |
| `MAVEN_CENTRAL_PASSWORD` | Central Portal password token |
| `SIGNING_KEY` | Private GPG key in ASCII armor format |
| `SIGNING_KEY_PASSWORD` | Passphrase for that key |

Without those secrets signing is skipped, so `publishToMavenLocal` works locally with no setup.

---

## 🤝 Contributing

Contributions are welcome.

1. Fork the repository
2. Create a branch: `git checkout -b feat/your-feature`
3. Make sure `./gradlew build` passes
4. Commit following [Conventional Commits](https://www.conventionalcommits.org/): `git commit -m "feat: add your feature"`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **GNU General Public License v3.0** — see the
[LICENSE](LICENSE) file for details.

---

<div align="center">
Made with ☕ by <a href="https://franciscosolis.cl">Fran</a>
</div>
