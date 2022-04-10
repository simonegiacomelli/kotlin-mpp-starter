package database.exposed.expansion

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IDateColumnType
import org.jetbrains.exposed.sql.javatime.JavaLocalDateTimeColumnType
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class KotlinxLocalDateTimeColumnType(val highResolution: Boolean) : ColumnType(), IDateColumnType {
    val del = JavaLocalDateTimeColumnType()
    override val hasTimePart: Boolean = true
    override fun sqlType(): String = fixSqlType()
    private fun fixSqlType(): String {
        val dtt = currentDialect.dataTypeProvider.dateTimeType()
            .removeSuffix("(6)")
        return dtt + (if (highResolution) "(6)" else "")
    }

    override fun nonNullValueToString(value: Any): String {
        val value = fix(value)
        if (value is java.time.LocalDateTime)
            return format(value)
        return del.nonNullValueToString(value)
    }

    private fun fix(value: Any): Any {
        val value = if (value is kotlinx.datetime.LocalDateTime) value.toJavaLocalDateTime() else value
        return value
    }

    override fun valueFromDB(value: Any): Any {
        val res = del.valueFromDB(value) as LocalDateTime?
        if (res != null) return res.toKotlinLocalDateTime()
        return super.valueFromDB(value.toString())
    }

    override fun notNullValueToDB(value: Any): Any {
        val value = fix(value)
        return when (value) {
            is LocalDateTime -> format(value)
            else -> value
        }
    }

    private fun format(value: LocalDateTime) =
        (if (highResolution) MICROSECOND_RESOLUTION else SECOND_RESOLUTION)
            .format(value.atZone(ZoneId.systemDefault()))
}


private val MICROSECOND_RESOLUTION by lazy {
    DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss.SSSSSS",
        Locale.ROOT
    ).withZone(ZoneId.systemDefault())
}
private val SECOND_RESOLUTION by lazy {
    DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss",
        Locale.ROOT
    ).withZone(ZoneId.systemDefault())
}