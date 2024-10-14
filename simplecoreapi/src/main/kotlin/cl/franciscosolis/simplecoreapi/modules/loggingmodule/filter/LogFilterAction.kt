package cl.franciscosolis.simplecoreapi.modules.loggingmodule.filter

/**
 * Interface representing the action to filter a message.
 */
interface LogFilterAction {

    /**
     * Filter the message and return the result.
     * @param message The message to filter.
     *
     * @return If null it will be hidden, otherwise it will show the new message.
     */
    fun filter(message: String): String?

}