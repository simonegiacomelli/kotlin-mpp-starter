package rpc.oneway

import api.oneway.ApiNotifyString
import rpc.OnewayContextHandlers
import rpc.oneway.topics.WsEndpoint

val onewayContextHandler = OnewayContextHandlers<OnewayContext>()

class OnewayContextHandler

class OnewayContext(val wsEndpoint: WsEndpoint)

private val reg1 = onewayContextHandler.register { req: ApiNotifyString, any ->
    println("ApiNotifyString: " + req.string)
}

