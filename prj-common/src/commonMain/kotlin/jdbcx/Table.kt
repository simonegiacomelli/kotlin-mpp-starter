package jdbcx

open class Table(tableName: String) {
    protected val columnMap = mutableMapOf<String, Column>()
    protected val columnIndex = mutableListOf<String>()

    val meta: TableMeta = object : TableMeta {
        override val tableName: String = tableName
        override val columns get() = columnIndex.map { columnMap[it] ?: error("Col not found: $it") }
        override val primaryKey: List<PrimaryKeyColumn> = emptyList()
    }

    fun long(name: String) = LongColumn(ColumnBaseDc(name, false)).register()
    fun integer(name: String) = IntColumn(ColumnBaseDc(name, false)).register()
    fun varchar(name: String, size: Int) = VarcharColumn(ColumnBaseDc(name, false), size).register()
    fun datetime(name: String, highResolution: Boolean = false) =
        DatetimeColumn(ColumnBaseDc(name, false), highResolution).register()

    protected fun <C : Column> C.register(): C = apply { columnMap[name] = this }
}

