package database.schema

object ac_sessions : Table(), CreatedAt, UpdatedAt {
    val id = varchar("id", 30)
    val user_id = long("user_id")

    //    val table_id = integer("table_id")
//    val fk_id = long("fk_id")
    override val created_at = createdAt()
    override val updated_at = updatedAt()
    override val primaryKey = PrimaryKey(id)
}