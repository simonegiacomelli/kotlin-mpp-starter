package context

import rpc.RpcMessage
import rpc.transport.http.*

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