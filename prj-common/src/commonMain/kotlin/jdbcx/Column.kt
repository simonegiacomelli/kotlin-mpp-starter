package jdbcx

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.reflect.KClass

interface Column : ColumnBase {
    val sqlType: String get() = error("Da implementare nella composite class columnName:$name")
    val createExpression: String get() = "$name $sqlType$nullableExpression"
    val kClass: KClass<*>
        get() = when (this) {
            is IntColumn -> Int::class
            is LongColumn -> Long::class
            is VarcharColumn -> String::class
            is DoubleColumn -> Double::class
            is DatetimeColumn -> LocalDateTime::class
            is DateColumn -> LocalDate::class
            is BitColumn -> Boolean::class
            else -> error("not implemented $this")
        }
    private val nullableExpression: String get() = if (nullable) "" else " NOT NULL"

    companion object {
        fun sensibleDefault(name: String, kClass: KClass<*>): Column {
            val base = ColumnBaseDc(name, true)
            return when (kClass) {
                Int::class -> IntColumn(base)
                Long::class -> LongColumn(base)
                String::class -> VarcharColumn(base, 4000)
                Double::class -> DoubleColumn(base)
                LocalDateTime::class -> DatetimeColumn(base)
                LocalDate::class -> DateColumn(base)
                Boolean::class -> BitColumn(base)
                else -> error("not implemented request name=`$name` kClass=`${kClass.simpleName}`")
            }
        }
    }
}