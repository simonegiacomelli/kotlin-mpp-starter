package time

/**
 * Created by Simone on 01/12/2015.
 */
class Backoff(private val delaySpan: Long, private val maxDelay: Long) {
    private var current: Long = 0
    private var iteration: Long = 0

    operator fun next(): Long {
        iteration++
        current += delaySpan
        if (current > maxDelay) current = maxDelay
        return current
    }

    fun iteration(): Long {
        return iteration
    }

    fun reset() {
        iteration = 0
        current = 0
    }
}