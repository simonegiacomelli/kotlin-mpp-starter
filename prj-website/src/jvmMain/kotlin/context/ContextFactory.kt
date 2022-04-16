package context

import accesscontrol.acUserFromSession
import appinit.State
import rpc.transport.http.RpcRequest

fun State.contextFactory(rpcRequest: RpcRequest): Context {
    val sessionId = rpcRequest.session_id
    val user = if (sessionId != null) database.acUserFromSession(sessionId) else Anonymous

    val state = this
    return object : Context {
        override val database = state.database
        override val user = user
    }
}

