package telemetry

import database.schema.tm_events
import database.schema.tm_types
import database.time.nowAtDefault
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun newEvent(database: Database, type_id: Int, arguments: String = "", created_at: LocalDateTime = nowAtDefault()) =
    transaction(database) {
        tm_events.insert {
            it[tm_events.type_id] = type_id
            it[tm_events.arguments] = arguments
            it[tm_events.created_at] = created_at
        }
    }

fun newEvent(
    database: Database,
    typeInfo: EventType,
    arguments: String = ""
) {
    val createdAt = nowAtDefault()
    transaction(database) {
        kotlin.runCatching {
            tm_types.insert {
                it[tm_types.id] = typeInfo.id
                it[tm_types.name] = typeInfo.name
                it[tm_types.created_at] = createdAt
            }
        }
    }
    newEvent(database, typeInfo.id, arguments, createdAt)
}
