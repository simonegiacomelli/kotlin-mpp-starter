package database.exposed


import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

class Exposed {

    companion object {
        fun logLevelInfo() {
            val log = LoggerFactory.getLogger("Exposed") as Logger
            log.level = Level.INFO
        }
    }
}