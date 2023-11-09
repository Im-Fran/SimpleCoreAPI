package xyz.theprogramsrc.simplecoreapi.global.modules.networkingmodule.models

data class Response(
    val request: Request,
    val code: Int,
    val message: String,
    val responseBody: ByteArray?,
    val headers: Map<String, List<String>>,
    val cookies: Map<String, String>,
    val error: Exception?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Response

        if (request != other.request) return false
        if (code != other.code) return false
        if (message != other.message) return false
        if (responseBody != null) {
            if (other.responseBody == null) return false
            if (!responseBody.contentEquals(other.responseBody)) return false
        } else if (other.responseBody != null) return false
        if (headers != other.headers) return false
        if (cookies != other.cookies) return false
        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int {
        var result = request.hashCode()
        result = 31 * result + code
        result = 31 * result + message.hashCode()
        result = 31 * result + (responseBody?.contentHashCode() ?: 0)
        result = 31 * result + headers.hashCode()
        result = 31 * result + cookies.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        return result
    }
}
