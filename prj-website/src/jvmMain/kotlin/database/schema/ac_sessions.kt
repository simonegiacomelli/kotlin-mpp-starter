package database.schema

const val session_id_length = 30

object ac_sessions : Table(), CreatedAt, UpdatedAt {
    val id = varchar("id", session_id_length)
    val user_id = long("user_id")
    override val created_at = createdAt()
    override val updated_at = updatedAt()
    override val primaryKey = PrimaryKey(id)
}


//    val table_id = integer("table_id")
//    val fk_id = long("fk_id")