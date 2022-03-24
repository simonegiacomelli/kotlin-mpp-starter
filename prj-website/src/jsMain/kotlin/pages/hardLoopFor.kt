package pages

import kotlinx.datetime.Clock

fun hardLoopFor(milliseconds: Int) {
    val start = Clock.System.now()
    while (true) {
        val duration = Clock.System.now().minus(start)
        if (duration.inWholeMilliseconds > milliseconds) break
    }
}
