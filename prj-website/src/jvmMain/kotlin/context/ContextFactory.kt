package context

import appinit.State
import rpc.transport.http.RpcRequest

fun State.contextFactory(rpcRequest: RpcRequest): Context {
    return object : Context {
        override val user: User = User(-1, "")

    }
}