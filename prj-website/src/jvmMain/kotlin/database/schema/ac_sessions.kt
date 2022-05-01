package database.schema

const val session_id_length = 30

object ac_sessions : Table(), CreatedAt, UpdatedAt {
    val id = varchar("id", session_id_length)
    val user_id = integer("user_id")
    val screen = varchar("screen", 50)
    val platform = varchar("platform", 100)
    val user_agent = text("user_agent")
    override val created_at = createdAt()
    override val updated_at = updatedAt()
    override val primaryKey = PrimaryKey(id)
}


//    val table_id = integer("table_id")
//    val fk_id = long("fk_id")