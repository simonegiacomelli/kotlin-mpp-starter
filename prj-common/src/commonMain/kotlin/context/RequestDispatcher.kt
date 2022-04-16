package context

import rpc.ContextHandlers
import rpc.transport.http.*

suspend fun <Context> requestDispatcher(
    ch: ContextHandlers<Context>,
    contextFactory: (RpcRequest) -> Context,
    httpRequest: suspend () -> HttpRequest
): HttpResponse =
    try {

        httpRequest().toRpcRequest().run {
            val payload = ch.dispatch(message, contextFactory(this))
            RpcResponse(Result.success(payload)).toHttpResponse()
        }

    } catch (ex: Exception) {
        println("Exception on api request ```${ex.stackTraceToString()}```")
        RpcResponse(Result.failure(ex)).toHttpResponse()
    }