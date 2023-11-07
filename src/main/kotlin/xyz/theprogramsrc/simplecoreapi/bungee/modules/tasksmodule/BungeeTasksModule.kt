package xyz.theprogramsrc.simplecoreapi.bungee.modules.tasksmodule

import net.md_5.bungee.api.scheduler.ScheduledTask
import net.md_5.bungee.api.scheduler.TaskScheduler
import xyz.theprogramsrc.simplecoreapi.bungee.BungeeLoader
import xyz.theprogramsrc.simplecoreapi.global.module.Module
import xyz.theprogramsrc.simplecoreapi.global.module.ModuleDescription
import xyz.theprogramsrc.simplecoreapi.global.modules.tasksmodule.models.RecurringTask
import java.util.concurrent.TimeUnit

class BungeeTasksModule: Module {


    companion object {
        /**
         * Instance of the [BungeeTasksModule].
         */
        lateinit var instance: BungeeTasksModule
            private set
    }

    override val description: ModuleDescription = ModuleDescription(
        name = "TasksModule",
        version = "0.3.0",
        authors = listOf("Im-Fran"),
    )

    /**
     * BungeeCord Scheduler
     */
    val scheduler: TaskScheduler  = BungeeLoader.instance.proxy.scheduler

    /**
     * Instance of Bungee Loader
     */
    private val plugin = BungeeLoader.instance

    override fun onEnable() {
        instance = this
    }

    override fun onDisable() {
    }

    /**
     * Runs an async task after the specified delay in ticks
     * @param delay The delay in ticks. Defaults to 1 (1 tick = 0.05 seconds)
     * @param task The task to run
     * @return the [ScheduledTask]
     */
    fun runAsync(delay: Int = 1, task: () -> Unit): ScheduledTask = scheduler.schedule(plugin, task, delay.times(50).toLong(), TimeUnit.MILLISECONDS)

    /**
     * Runs a repeating task asynchronously every given ticks (1 tick = 0.05 seconds) after the given ticks (1 tick = 0.05 seconds)
     * @param delay The delay in ticks. Defaults to 1
     * @param period The period in ticks. Defaults to 1
     * @param task The task to run
     * @return the [RecurringTask]
     */
    fun runAsyncRepeating(delay: Int = 1, period: Int = 1, task: () -> Unit): RecurringTask {
        val bungeeTask = scheduler.schedule(plugin, task, delay.times(50).toLong(), period.times(50).toLong(), TimeUnit.MILLISECONDS)
        return object: RecurringTask(){
            var cancelled: Boolean = false

            override fun start(): RecurringTask = this.apply {
                if(cancelled) {
                    this@BungeeTasksModule.runAsyncRepeating(delay, period, task)
                    cancelled = false
                }
            }

            override fun stop(): RecurringTask = this.apply {
                bungeeTask.cancel()
                cancelled = true
            }

        }
    }

}