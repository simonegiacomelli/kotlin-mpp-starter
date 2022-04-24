package jdbcx

interface TableMeta {
    /** Il nome e' sempre in lowercase */
    val tableName: String

    /** Il nome colonna e' sempre in lowercase */
    val columns: List<Column>
    val partitionsClause: String get() = ""
    val primaryKey: List<PrimaryKeyColumn>
}

