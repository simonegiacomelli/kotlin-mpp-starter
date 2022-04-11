package appinit

import folders.data.etc.Config
import jdbc.Jdbc
import time.Backoff

fun Config.waitJdbcInfo(): Jdbc {
    val backoff = Backoff(1, 60)
    log.i("Database connection probing...")
    while (true) {
        val jdbc = database
        val result = runCatching {
            jdbc.registerDriver()
            jdbc.newConnection()
        }
        val connection = result.getOrNull()
        if (connection != null) {
            log.i("Database connection is ok.")
            connection.close()
            return jdbc
        }
        val next = backoff.next()
        log.i("WARNING. db connection failed. Delay for $next seconds. Exception=${result.exceptionOrNull().toStr()}")
        Thread.sleep(next * 1000)
    }
}

private fun Throwable?.toStr(): String {
    val e = this ?: return "null"
    return e.stackTraceToString().replace("\n", "\\n")
}
