package database.exposed

import org.jetbrains.exposed.sql.Database
import java.sql.Connection

interface TempDbBase {
    fun create()
    fun connection(): Connection
    val database: Database
    fun remove()
}