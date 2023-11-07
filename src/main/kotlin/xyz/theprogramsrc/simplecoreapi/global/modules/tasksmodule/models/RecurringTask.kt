package xyz.theprogramsrc.simplecoreapi.global.modules.tasksmodule.models

abstract class RecurringTask {

    abstract fun start(): RecurringTask

    abstract fun stop(): RecurringTask
}