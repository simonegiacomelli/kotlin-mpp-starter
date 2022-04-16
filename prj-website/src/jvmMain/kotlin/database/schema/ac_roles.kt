package database.schema

object ac_roles : Table() {
    val id = integer("id")
    val name = varchar("name", 256)
    override val primaryKey = PrimaryKey(id)
}
