package context

import rpc.ContextHandlers
import rpc.transport.http.*

class ContextRequest<Context>(
    private val ch: ContextHandlers<Context>, private val contextFactory: RpcRequest.() -> Context
) {

    suspend fun process(httpRequest: suspend () -> HttpRequest): HttpResponse = try {
        httpRequest().toRpcRequest().run {
            val payload = ch.dispatch(message, contextFactory())
            RpcResponse(Result.success(payload)).toHttpResponse()
        }

    } catch (ex: Exception) {
        println("Exception on api request ```${ex.stackTraceToString()}```")
        RpcResponse(Result.failure(ex)).toHttpResponse()
    }

}
