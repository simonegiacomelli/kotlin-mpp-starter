package rpc.transport.http

import rpc.RpcMessage
import rpc.rpcHttpHandlerName

data class HttpRequest(
    val body: String,
    val headers: Map<String, String>,
    val url: String,
    val parameters: Map<String, String>,
)

data class HttpResponse(
    val body: String,
    val headers: Map<String, String>,
    val status: Int,
    val clientException: Exception?,
)

class RpcRequest(val message: RpcMessage, val session_id: String?)

private const val session_id_key = "id"
fun RpcRequest.toHttpRequest(apiBaseUrl: String) = HttpRequest(
    message.payload,
    mapOf(session_id_key to (session_id ?: "")),
    "$apiBaseUrl$rpcHttpHandlerName",
    mapOf("api_name" to message.name)
)

fun HttpRequest.toRpcRequest() = RpcRequest(
    RpcMessage(
        parameters["api_name"] ?: error("Don't know which api to call. httpRequest=```$this```"),
        body
    ),
    headers.getOrElse(session_id_key) { "" }
)

class RpcResponse(val result: Result<String>)

private const val successStatus = 200
private const val failureStatus = 500

fun RpcResponse.toHttpResponse() =
    if (result.isSuccess)
        result.getOrThrow().run { HttpResponse(this, emptyMap(), successStatus, null) }
    else
        result.exceptionOrNull()!!.run { HttpResponse(stackTraceToString(), emptyMap(), failureStatus, null) }

fun HttpResponse.toRpcResponse() = if (status == successStatus)
    RpcResponse(Result.success(body))
else
    RpcResponse(Result.failure(HttpResponseException(this)))

class HttpResponseException(val httpResponse: HttpResponse) :
    Exception(httpResponse.run { "HttpResponse with status=$status" })
