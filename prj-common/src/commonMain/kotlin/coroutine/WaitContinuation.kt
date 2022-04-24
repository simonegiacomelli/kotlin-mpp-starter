package coroutine

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WaitContinuation<T>(private val description: String = "") {
    private var continuation: Continuation<T>? = null
    fun resume(value: T) {
        val c = continuation
        if (c == null) {
            val msg = "Error, continuation is null ($description)"
            println(msg)
            error(msg)
        }
        c.resume(value)
    }

    suspend fun runWaitResume(function: (Continuation<T>) -> Unit): T =
        suspendCoroutine { continuation = it; function(it) }
}