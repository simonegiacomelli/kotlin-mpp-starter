package rpc.oneway.topics

import kotlinx.datetime.Clock
import rpc.RpcMessage
import rpc.sendOneway

open class WsEndpoint {
    val createdTime = Clock.System.now()
}

abstract class WsEndpointAnswerable : WsEndpoint() {
    abstract fun send(string: String)
}

fun WsEndpoint.strRepr() = "$createdTime"

fun WsEndpointAnswerable.dispatcherOneway(apiName: String, payload: String) {
    println("WsEndpoint.dispatcherOneway $apiName $payload")
    send(RpcMessage.encode(apiName, payload))
}

inline fun <reified Req> WsEndpointAnswerable.onewayApiSend(
    request: Req,
) {
    sendOneway(request, ::dispatcherOneway)
}

