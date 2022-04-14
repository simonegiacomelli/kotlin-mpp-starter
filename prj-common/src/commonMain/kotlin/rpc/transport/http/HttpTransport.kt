package rpc.transport.http

import rpc.RpcMessage
import rpc.rpcHttpHandlerName

// interface to fetch/XMLHttpRequest
// interface to api server/servlet-container/etc
fun interface HttpTransport {
    fun process(httpRequest: HttpRequest): HttpResponse
}

class HttpRequest(
    val body: String,
    val headers: Map<String, String>,
    val url: String,
    val parameters: Map<String, String>,
)

class HttpResponse(
    val body: String,
    val headers: Map<String, String>,
    val status: Int,
    val clientException: Exception?,
)

class RpcRequest(val message: RpcMessage, val session_id: String)

fun RpcRequest.toHttpRequest(apiBaseUrl: String) = HttpRequest(
    message.payload,
    mapOf("session_id" to session_id),
    "$apiBaseUrl$rpcHttpHandlerName",
    mapOf("api_name" to message.simpleName)
)

private val errorMessage = "Don't know which api to call"
fun HttpRequest.toRpcRequest() = RpcRequest(
    RpcMessage(parameters["api_name"] ?: error(errorMessage), body),
    headers.getOrElse("session_id") { "" }
)

class RpcResponse(val result: Result<String>)

fun RpcResponse.toHttpResponse() = if (result.isSuccess)
    result.getOrThrow().run {
        HttpResponse(this, emptyMap(), 200, null)
    } else result.exceptionOrNull()!!.run {
    HttpResponse(stackTraceToString(), emptyMap(), 555, null)
}

fun HttpResponse.toRpcResponse() = if (status == 200)
    RpcResponse(Result.success(body))
else
    RpcResponse(Result.failure(HttpResponseException(this)))

class HttpResponseException(val httpResponse: HttpResponse) :
    Exception(httpResponse.run { "HttpResponse with status=$status" })
