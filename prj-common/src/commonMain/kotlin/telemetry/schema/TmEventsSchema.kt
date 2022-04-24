package telemetry.schema

import jdbcx.Table

object TmEventsSchema : Table("tm_events") {
    val id = long("id")
    val type_id = integer("event_type")
    val arguments = varchar("arguments", 200)
    val created_at = datetime("created_at", highResolution = true)
}