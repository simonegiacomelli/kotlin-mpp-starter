package database.schema

object ac_user_roles : Table() {
    val user_id = integer("user_id")
    val role_id = integer("role_id")
    override val primaryKey = PrimaryKey(user_id, role_id)
}
