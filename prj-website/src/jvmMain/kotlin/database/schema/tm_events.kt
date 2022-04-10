package database.schema

object tm_events : LongIdTable(), CreatedAt {
    val type_id = integer("event_type")
    val arguments = varchar("arguments", 200)
    override val created_at = datetime("created_at")
}