/*
 * SimpleCoreAPI - Kotlin Project Library
 * Copyright (C) 2024 Francisco Solís
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cl.franciscosolis.simplecoreapi.bukkit.modules.tasksmodule

import cl.franciscosolis.simplecoreapi.bukkit.BukkitLoader
import cl.franciscosolis.simplecoreapi.module.Module
import cl.franciscosolis.simplecoreapi.module.ModuleDescription
import cl.franciscosolis.simplecoreapi.modules.tasksmodule.models.RecurringTask
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask

class BukkitTasksModule: Module {


    companion object {
        /**
         * Instance of the [BukkitTasksModule].
         */
        lateinit var instance: BukkitTasksModule
            private set
    }

    override val description: ModuleDescription = ModuleDescription(
        name = "TasksModule",
        version = "0.3.0",
        authors = listOf("Im-Fran"),
    )

    /**
     * Instance of the [BukkitScheduler] (Spigot Scheduler)
     */
    val scheduler: BukkitScheduler = Bukkit.getScheduler()

    /**
     * Instance of Spigot Loader
     */
    private val plugin = BukkitLoader.instance

    override fun onEnable() {
        instance = this
    }

    override fun onDisable() {
    }

    /**
     * Runs a task after 1 tick (0.05 seconds)
     * @param task The task to run
     * @return the [BukkitTask]
     */
    fun runTask(task: Runnable): BukkitTask = runTaskLater(task = task)

    /**
     * Runs an async task after 1 tick (0.05 seconds)
     * @param task The task to run
     * @return the [BukkitTask]
     */
    fun runTaskAsynchronously(task: Runnable): BukkitTask = runTaskLaterAsynchronously(task = task)

    /**
     * Runs a task after the given ticks (1 tick = 0.05 seconds)
     * @param ticks The ticks to run after. Defaults to 1 tick (0.05 seconds)
     * @param task The task to run
     * @return the [BukkitTask]
     */
    fun runTaskLater(ticks: Long = 1, task: Runnable): BukkitTask = scheduler.runTaskLater(plugin, task, ticks)

    /**
     * Runs an async task after the given ticks (1 tick = 0.05 seconds)
     * @param ticks The ticks to run after. Defaults to 1 tick (0.05 seconds)
     * @param task The task to run
     * @return the [BukkitTask]
     */
    fun runTaskLaterAsynchronously(ticks: Long = 1, task: Runnable): BukkitTask = scheduler.runTaskLaterAsynchronously(plugin, task, ticks)

    /**
     * Runs a repeating task every given ticks (1 tick = 0.05 seconds) after the given ticks (1 tick = 0.05 seconds)
     * @param period The ticks to wait between each run. Defaults to 1 tick (0.05 seconds)
     * @param delay The ticks to wait before the first run. Defaults to 1 tick (0.05 seconds)
     * @param manualInit If the task should be initialized manually. Defaults to false
     * @param task The task to run
     * @return the [RecurringTask]
     */
    fun runTaskTimer(period: Long = 1, delay: Long = 1, manualInit: Boolean = false, task: Runnable): RecurringTask = createRecurringTask(manualInit = manualInit){
        scheduler.runTaskTimer(plugin, task, delay, period)
    }

    /**
     * Runs a repeating task asynchronously every given ticks (1 tick = 0.05 seconds) after the given ticks (1 tick = 0.05 seconds)
     * @param period The ticks to wait between each run. Defaults to 1 tick (0.05 seconds)
     * @param delay The ticks to wait before the first run. Defaults to 1 tick (0.05 seconds)
     * @param manualInit If the task should be initialized manually. Defaults to false
     * @param task The task to run
     * @return the [RecurringTask]
     */
    fun runTaskTimerAsynchronously(period: Long = 1, delay: Long = 1, manualInit: Boolean = false, task: Runnable): RecurringTask = createRecurringTask(manualInit = manualInit){
        scheduler.runTaskTimerAsynchronously(plugin, task, delay, period)
    }

    private fun createRecurringTask(manualInit: Boolean = false, bukkitTask: () -> BukkitTask): RecurringTask {
        return object:RecurringTask {
            private var task: BukkitTask? = null

            init {
                if(!manualInit) {
                    task = bukkitTask.invoke()
                }
            }

            override fun start(): RecurringTask {
                if(task == null){
                    task = bukkitTask.invoke()
                    return this
                }

                if(!scheduler.isCurrentlyRunning(task!!.taskId)) {
                    task = bukkitTask.invoke()
                }
                return this
            }

            override fun stop(): RecurringTask {
                task?.cancel()
                task = null
                return this
            }
        }
    }

}