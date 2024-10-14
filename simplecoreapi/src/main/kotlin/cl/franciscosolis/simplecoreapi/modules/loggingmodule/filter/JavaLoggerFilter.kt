package cl.franciscosolis.simplecoreapi.modules.loggingmodule.filter

import java.util.logging.Filter
import java.util.logging.LogRecord

/**
 * Representation of a LogFilter for Java Logger. This can be used to filter messages from the console.
 *
 * @param logFilterAction The function to use to filter the messages and return the result of whether to filter or not.
 */
class JavaLoggerFilter(private val logFilterAction: LogFilterAction): Filter {

    override fun isLoggable(record: LogRecord): Boolean {
        val filter = this.logFilterAction.filter(record.message)
        if(filter != null) {
            record.message = filter
        }

        return filter != null
    }
}