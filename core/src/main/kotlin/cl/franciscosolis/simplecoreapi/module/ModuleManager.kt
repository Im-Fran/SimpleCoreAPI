package cl.franciscosolis.simplecoreapi.module

import java.lang.reflect.InvocationTargetException

/**
 * Global, lazy module registry.
 *
 * A module is instantiated the first time someone asks for it, and that instance is reused
 * for the rest of the server's life. Nothing is loaded on startup.
 */
public object ModuleManager {

    /** Live instances, in load order. */
    private val modules = LinkedHashMap<Class<*>, Module>()

    /** Modules currently being constructed, used to detect circular dependencies. */
    private val loading = LinkedHashSet<Class<*>>()

    /** Currently loaded modules, in load order. */
    public val loaded: List<Class<*>>
        @Synchronized get() = modules.keys.toList()

    /**
     * Returns the instance of [clazz], creating it on first use.
     *
     * Prefer the shorter `requireModule<T>()` function.
     *
     * This method is `@Synchronized` on an `object`, which makes it reentrant: a module can
     * request other modules while it is still being constructed.
     * `ConcurrentHashMap.computeIfAbsent` would fail in that case.
     */
    @Synchronized
    public fun <T : Module> require(clazz: Class<T>): T {
        modules[clazz]?.let { return clazz.cast(it) }

        check(loading.add(clazz)) {
            "Circular module dependency: " +
                (loading.toList() + clazz).joinToString(" -> ") { it.simpleName }
        }

        try {
            val module = try {
                clazz.getDeclaredConstructor().newInstance()
            } catch (e: NoSuchMethodException) {
                throw IllegalStateException(
                    "Module ${clazz.name} needs a public no-argument constructor",
                    e,
                )
            } catch (e: InvocationTargetException) {
                // Propagate the real constructor failure instead of the reflection wrapper:
                // otherwise a circular dependency error would arrive unrecognizable.
                when (val cause = e.targetException) {
                    is RuntimeException -> throw cause
                    is Error -> throw cause
                    else -> throw IllegalStateException("Module ${clazz.name} failed to initialize", cause)
                }
            } catch (e: ReflectiveOperationException) {
                throw IllegalStateException("Could not instantiate module ${clazz.name}", e)
            }

            module.onEnable()
            modules[clazz] = module
            return module
        } finally {
            loading.remove(clazz)
        }
    }

    /** `true` if [clazz] has already been instantiated. Useful to avoid forcing its load. */
    @Synchronized
    public fun isLoaded(clazz: Class<out Module>): Boolean = clazz in modules

    /**
     * Disables every module in reverse load order and clears the registry.
     *
     * Called by each platform bootstrap on `onDisable`. A failing [Module.onDisable] is
     * logged but does not stop the remaining modules from shutting down.
     */
    @Synchronized
    public fun disableAll() {
        modules.entries.reversed().forEach { (clazz, module) ->
            runCatching { module.onDisable() }.onFailure {
                cl.franciscosolis.simplecoreapi.SimpleCoreAPI.logger
                    .warning("Failed to disable module ${clazz.simpleName}: ${it.message}")
            }
        }
        modules.clear()
        loading.clear()
    }
}

/**
 * Returns the instance of module [T], creating it on first use.
 *
 * ```kotlin
 * val commands = requireModule<CommandModule>()
 * commands.registerCommand("login") { sender, args -> }
 * ```
 */
public inline fun <reified T : Module> requireModule(): T = ModuleManager.require(T::class.java)
