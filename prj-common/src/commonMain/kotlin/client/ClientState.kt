package client

import kotlinx.coroutines.CoroutineScope
import rpc.Request
import rpc.sendRequest

interface ClientState {
    fun toast(message: String)
    fun spinner(function: suspend CoroutineScope.() -> Unit)
    val ApiBaseUrl: String
    val session_id: String?
    suspend fun dispatch(name: String, payload: String): String
    suspend fun launch(block: suspend CoroutineScope.() -> Unit)
}

suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> ClientState.send(request: Req):
        Resp = request.sendRequest(::dispatch)


var clientStateOrNull: () -> ClientState = { error("non ClientState handler") }
val clientState get() = clientStateOrNull()