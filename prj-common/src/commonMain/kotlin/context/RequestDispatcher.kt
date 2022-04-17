package context

import rpc.RpcMessage
import rpc.transport.http.*

class RequestDispatcher<Context>(
    val dispatcher: (RpcMessage, Context) -> String,
    val contextFactory: (RpcRequest) -> Context,
    val authorized: (RpcMessage, Context) -> Int = { _, _ -> 200 },
    val httpRequest: suspend () -> HttpRequest
) {
    suspend fun dispatch(): HttpResponse = run {
        try {
            val httpRequest1 = httpRequest()
            val rpcRequest = httpRequest1.toRpcRequest()
            val context = contextFactory(rpcRequest)
            val message = rpcRequest.message
            val authStatus = authorized(message, context)
            if (authStatus != 200) return@run HttpResponse("unauthorized", emptyMap(), authStatus, null)
            val payload = dispatcher(message, context)
            RpcResponse(Result.success(payload)).toHttpResponse()
        } catch (ex: Exception) {
            println("Exception on api request ```${ex.stackTraceToString()}```")
            RpcResponse(Result.failure(ex)).toHttpResponse()
        }
    }
}

suspend fun <Context> requestDispatcher(
    dispatcher: (RpcMessage, Context) -> String,
    contextFactory: (RpcRequest) -> Context,
    httpRequest: suspend () -> HttpRequest
): HttpResponse =
    try {

        httpRequest().toRpcRequest().run {
            val payload = dispatcher(message, contextFactory(this))
            RpcResponse(Result.success(payload)).toHttpResponse()
        }

    } catch (ex: Exception) {
        println("Exception on api request ```${ex.stackTraceToString()}```")
        RpcResponse(Result.failure(ex)).toHttpResponse()
    }