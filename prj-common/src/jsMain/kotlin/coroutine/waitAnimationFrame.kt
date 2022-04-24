package coroutine

import kotlinx.browser.window

suspend fun waitAnimationFrame() {
    WaitContinuation<Unit>("waitAnimationFrame").apply {
        runWaitResume { window.requestAnimationFrame { resume(Unit) } }
    }
}