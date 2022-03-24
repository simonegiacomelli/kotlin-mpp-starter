package time

import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun <T> printTime(msg: String, block: () -> T): T {
    val (value, duration) = measureTimedValue { block() }
    println("$msg took $duration")
    return value
}