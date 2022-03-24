package rpc.oneway

import rpc.sendOneway

abstract class Transport {
    abstract fun dispatcherOneway(apiName: String, payload: String): Unit
    inline fun <reified Req> send(request: Req) = sendOneway(request, ::dispatcherOneway)
}
