package appinit

import folders.Folders
import folders.data.etc.config
import jdbc.Jdbc
import time.Backoff

fun Folders.waitJdbcInfo(): Jdbc {
    val backoff = Backoff(1, 60)
    log.i("Database connection probing...")
    while (true) {
        val jdbc = config().database
        val urlMsg = "url=`${jdbc.url}`"
        val result = runCatching {
            jdbc.registerDriver()
            jdbc.newConnection()
        }
        val connection = result.getOrNull()
        if (connection != null) {
            log.i("Database connection is ok. $urlMsg")
            connection.close()
            return jdbc
        }
        val next = backoff.next()
        log.i(
            "WARNING. db connection failed. $urlMsg. Delay for $next seconds. Exception=${
                result.exceptionOrNull().toStr()
            }"
        )
        Thread.sleep(next * 1000)
    }
}

private fun Throwable?.toStr(): String {
    val e = this ?: return "null"
    return e.stackTraceToString().replace("\n", "\\n")
}
