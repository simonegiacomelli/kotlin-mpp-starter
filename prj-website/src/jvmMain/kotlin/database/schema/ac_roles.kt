package database.schema

object ac_roles : Table(), CreatedAt, UpdatedAt {
    val id = integer("id")
    val name = varchar("name", 256)
    override val created_at = createdAt()
    override val updated_at = updatedAt()
    override val primaryKey = PrimaryKey(id)
}
