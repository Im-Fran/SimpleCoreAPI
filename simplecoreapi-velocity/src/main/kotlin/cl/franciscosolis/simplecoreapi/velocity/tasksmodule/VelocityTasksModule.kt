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

package cl.franciscosolis.simplecoreapi.velocity.tasksmodule

import cl.franciscosolis.simplecoreapi.module.Module
import cl.franciscosolis.simplecoreapi.module.ModuleDescription
import cl.franciscosolis.simplecoreapi.modules.tasksmodule.models.RecurringTask
import com.velocitypowered.api.scheduler.ScheduledTask
import com.velocitypowered.api.scheduler.Scheduler
import cl.franciscosolis.simplecoreapi.velocity.VelocityLoader
import java.util.concurrent.TimeUnit

/**
 * Representation of the [VelocityTasksModule].
 */
class VelocityTasksModule: Module {

    companion object {
        /**
         * Instance of the [VelocityTasksModule].
         */
        lateinit var instance: VelocityTasksModule
            private set
    }

    /**
     * Instance of the [com.velocitypowered.api.scheduler.Scheduler]
     * from the [com.velocitypowered.api.proxy.ProxyServer].
     */
    val scheduler: Scheduler = VelocityLoader.instance.server.scheduler

    /**
     * Instance of the [VelocityLoader]
     */
    private val plugin = VelocityLoader.instance


    override val description: ModuleDescription = ModuleDescription(
        name = "TasksModule",
        version = "0.3.0",
        authors = listOf("Im-Fran"),
    )

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
    fun runAsync(delay: Int = 1, task: () -> Unit): ScheduledTask =
        scheduler.buildTask(plugin, task)
            .delay(delay.times(50).toLong(), TimeUnit.MILLISECONDS)
            .schedule()

    /**
     * Runs a repeating task asynchronously every given ticks (1 tick = 0.05 seconds) after the given ticks (1 tick = 0.05 seconds)
     * @param delay The delay in ticks. Defaults to 1
     * @param period The period in ticks. Defaults to 1
     * @param task The task to run
     * @return the [RecurringTask]
     */
    fun runAsyncRepeating(delay: Int = 1, period: Int = 1, task: () -> Unit): RecurringTask {
        val velocityTask = scheduler.buildTask(plugin, task)
            .delay(delay.times(50).toLong(), TimeUnit.MILLISECONDS)
            .repeat(period.times(50).toLong(), TimeUnit.MILLISECONDS)
            .schedule()
        return object: RecurringTask {
            var cancelled: Boolean = false

            override fun start(): RecurringTask = this.apply {
                if(cancelled) {
                    this@VelocityTasksModule.runAsyncRepeating(delay, period, task)
                    cancelled = false
                }
            }

            override fun stop(): RecurringTask = this.apply {
                velocityTask.cancel()
                cancelled = true
            }

        }
    }
}