package state

import accesscontrol.Session
import accesscontrol.UserAbs
import kotlinx.coroutines.CoroutineScope

interface ClientState {
    fun toast(message: String)
    fun spinner(function: suspend CoroutineScope.() -> Unit)
    val ApiBaseUrl: String
    val session_id: String?
    val user: UserAbs
    var sessionOrNull: Session?
    suspend fun dispatch(name: String, payload: String): String
    fun launch(block: suspend CoroutineScope.() -> Unit)
}
