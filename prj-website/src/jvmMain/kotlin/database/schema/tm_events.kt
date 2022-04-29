package database.schema

object tm_events : LongIdTable(), CreatedAt {
    val type_id = integer("type_id")
    val arguments = varchar("arguments", 200)
    override val created_at = createdAt()
}