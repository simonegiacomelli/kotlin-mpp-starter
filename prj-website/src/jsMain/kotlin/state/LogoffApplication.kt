package state

import api.names.ApiAcLogoffRequest
import kotlinx.browser.window
import kotlinx.coroutines.withTimeout
import rpc.send

fun JsState.logoffApplication() = spinner {
    runCatching { withTimeout(1000) { session_id?.also { ApiAcLogoffRequest(it).send() } } }
    sessionOrNull = null
    window.setTimeout({ coroutine.launch { startupApplication() } }, 1)
}