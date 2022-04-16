package client

import accesscontrol.Session
import kotlinx.coroutines.CoroutineScope

interface State {
    fun toast(message: String)
    fun spinner(function: suspend CoroutineScope.() -> Unit)
    val ApiBaseUrl: String
    val session_id: String?
    var sessionOrNull: Session?
    suspend fun dispatch(name: String, payload: String): String
    suspend fun launch(block: suspend CoroutineScope.() -> Unit)
}
