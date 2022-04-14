package rpc.http

import rpc.rpcHttpHandlerName

class FetchRequest(
    val ApiBaseUrl: String,
    val apiName: String,
    val payload: String,
    val sessionId: String,
) {
    fun values(): FetchValues {
        val url = "$ApiBaseUrl$rpcHttpHandlerName?api_name=$apiName"
        return FetchValues(url, payload)
    }
}

class FetchValues(
    val url: String,
    val body: String,
    val headers: Map<String, String> = emptyMap(),
    val method: String = "POST",
)

class FetchResponse(val status: Short, val body: String) {
    fun payload(): String {
        val response = body.split("\n\n", limit = 2)
        if (response[0] == "success=1")
            return response[1]
        error(response[1])
    }
}