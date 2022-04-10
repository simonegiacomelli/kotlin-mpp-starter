package database.schema

object tm_types : Table(), CreatedAt {
    val id = integer("id")
    val name = varchar("name", 200)
    override val created_at = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}