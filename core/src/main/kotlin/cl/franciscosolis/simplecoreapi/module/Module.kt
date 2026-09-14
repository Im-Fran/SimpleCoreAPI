package cl.franciscosolis.simplecoreapi.module

/**
 * A unit of functionality loaded on demand by [ModuleManager].
 *
 * A module class only needs to:
 *  - implement this interface
 *  - have a public no-argument constructor
 *
 * There are no annotations, no registration step and no classpath scanning. Any plugin can
 * define its own modules and expose them to other plugins.
 *
 * A module may request other modules with `requireModule` from its constructor or its
 * [onEnable]; circular dependencies are detected and reported with the full chain.
 */
public interface Module {

    /** Runs exactly once, right after the module is instantiated. */
    public fun onEnable() {}

    /** Runs when SimpleCoreAPI shuts down, in reverse load order. */
    public fun onDisable() {}
}
