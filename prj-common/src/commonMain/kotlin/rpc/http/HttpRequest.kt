package rpc.http

import rpc.ContextHandlers

class HttpRequest(
    val parameters: Map<String, List<String>>,
    val headers: Map<String, List<String>>,
    val receivedBody: String
) {
    companion object {
        fun exception(ex: Exception): HttpResponse {
            val text = "success=0\n\n${ex.stackTraceToString()}"
            println("handling exception [[$text]] ")
            return HttpResponse(text, status = 500)
        }
    }

    fun <C> dispatch(
        contextHandlers: ContextHandlers<C>,
        contextCreator: () -> C
    ): HttpResponse {
        val apiName = (parameters["api_name"] ?: emptyList()).firstOrNull() ?: error("Don't know which api to call")
        println("dispatching: $apiName")
        val serializedResponse = contextHandlers.dispatch(apiName, receivedBody, contextCreator())
        return HttpResponse("success=1\n\n$serializedResponse")
    }
}

