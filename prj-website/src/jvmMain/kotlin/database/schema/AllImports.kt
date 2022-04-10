package database.schema

import database.exposed.expansion.datetime
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

fun Table.datetime(name: String, highResolution: Boolean = false) = datetime(name, highResolution)

typealias Table = Table
typealias LongIdTable = LongIdTable

interface ModelCreated {
    val created_at: Column<LocalDateTime>
    val updated_at: Column<LocalDateTime>
}

interface ModelUpdated {
    val created_at: Column<LocalDateTime>
}

interface ModelDeleted {
    val deleted_at: Column<LocalDateTime>
}