package client

import kotlinx.coroutines.CoroutineScope
import rpc.Request
import rpc.sendRequest

interface StateAbs {
    fun toast(message: String)
    fun spinner(function: suspend CoroutineScope.() -> Unit)
    val ApiBaseUrl: String
    val session_id: String?
    suspend fun dispatch(name: String, payload: String): String
    suspend fun launch(block: suspend CoroutineScope.() -> Unit)
}

suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> StateAbs.send(request: Req):
        Resp = request.sendRequest(::dispatch)


var clientStateOrNull: () -> StateAbs = { error("no ClientState handler") }
val clientState get() = clientStateOrNull()