package database.schema

import database.exposed.expansion.datetime
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

fun Table.datetime(name: String, highResolution: Boolean = false) = datetime(name, highResolution)

typealias Table = Table
typealias LongIdTable = LongIdTable

interface CreatedAt {
    val created_at: Column<LocalDateTime>
    fun Table.createdAt() = datetime("created_at", highResolution = true)
}

interface UpdatedAt {
    val updated_at: Column<LocalDateTime>
    fun Table.updatedAt() = datetime("updated_at", highResolution = true)
}

interface DeletedAt {
    val deleted_at: Column<LocalDateTime>
}


