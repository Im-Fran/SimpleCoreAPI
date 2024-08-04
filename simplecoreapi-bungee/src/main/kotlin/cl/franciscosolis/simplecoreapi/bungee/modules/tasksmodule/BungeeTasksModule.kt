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

package cl.franciscosolis.simplecoreapi.bungee.modules.tasksmodule

import cl.franciscosolis.simplecoreapi.bungee.BungeeLoader
import cl.franciscosolis.simplecoreapi.module.Module
import cl.franciscosolis.simplecoreapi.module.ModuleDescription
import cl.franciscosolis.simplecoreapi.modules.tasksmodule.models.RecurringTask
import net.md_5.bungee.api.scheduler.ScheduledTask
import net.md_5.bungee.api.scheduler.TaskScheduler
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
     * @param manualInit If the task should be manually initialized. Defaults to false
     * @param task The task to run
     * @return the [RecurringTask]
     */
    fun runAsyncRepeating(delay: Int = 1, period: Int = 1, manualInit: Boolean = false, task: () -> Unit): RecurringTask {
        var taskId: Int? = null
        if(!manualInit) {
            taskId = scheduler.schedule(plugin, task, delay.times(50).toLong(), period.times(50).toLong(), TimeUnit.MILLISECONDS).id
        }

        return object: RecurringTask {
            var cancelled: Boolean = manualInit

            override fun start(): RecurringTask = this.apply {
                if(cancelled) {
                    this@BungeeTasksModule.runAsyncRepeating(delay, period, manualInit = false, task)
                    cancelled = false
                }
            }

            override fun stop(): RecurringTask = this.apply {
                if(taskId != null) {
                    scheduler.cancel(taskId)
                }
                cancelled = true
            }

        }
    }

}