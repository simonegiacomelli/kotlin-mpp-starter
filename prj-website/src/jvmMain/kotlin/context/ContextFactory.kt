package context

import accesscontrol.Anonymous
import accesscontrol.UserAbs
import accesscontrol.acGetUserAndRefresh
import appinit.State
import rpc.transport.http.RpcRequest

fun State.contextFactory(rpcRequest: RpcRequest): Context {
    val sessionIdOrNull = rpcRequest.session_id
    val user: UserAbs? = sessionIdOrNull?.let { sessionId -> acGetUserAndRefresh(sessionId) }
    val state = this
    return object : Context {
        override val database = state.database
        override val user = user ?: Anonymous
    }
}
