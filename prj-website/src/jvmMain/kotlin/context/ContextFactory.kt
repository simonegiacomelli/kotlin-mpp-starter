package context

import accesscontrol.acGetUserAndRefresh
import appinit.State
import rpc.transport.http.RpcRequest

fun State.contextFactory(rpcRequest: RpcRequest): Context {
    val sessionIdOrNull = rpcRequest.session_id
    val user: User? = sessionIdOrNull?.let { sessionId -> acGetUserAndRefresh(sessionId) }
    val state = this
    return object : Context {
        override val database = state.database
        override val user = user ?: Anonymous
    }
}
