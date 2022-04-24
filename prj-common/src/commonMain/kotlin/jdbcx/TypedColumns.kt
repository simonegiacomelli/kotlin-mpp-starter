package jdbcx

import kotlinx.serialization.Serializable

@Serializable
data class IntColumn(val column: ColumnBase, override val sqlType: String = "INT") : Column, ColumnBase by column

@Serializable
data class LongColumn(val column: ColumnBase, override val sqlType: String = "BIGINT") : Column, ColumnBase by column

@Serializable
data class DatetimeColumn(val column: ColumnBase, val highResolution: Boolean = false) : Column, ColumnBase by column

@Serializable
data class DateColumn(val column: ColumnBase) : Column, ColumnBase by column

@Serializable
data class TimeColumn(val column: ColumnBase) : Column, ColumnBase by column

@Serializable
data class DoubleColumn(val column: ColumnBase) : Column, ColumnBase by column

@Serializable
data class BigintColumn(val column: ColumnBase) : Column, ColumnBase by column

@Serializable
data class BitColumn(val column: ColumnBase) : Column, ColumnBase by column

@Serializable
data class LongtextColumn(val column: ColumnBase) : Column, ColumnBase by column

@Serializable
data class VarbinaryColumn(val column: ColumnBase, val size: Int) : Column, ColumnBase by column

@Serializable
data class VarcharColumn(val column: ColumnBase, val size: Int, override val sqlType: String = "VARCHAR($size)") :
    Column, ColumnBase by column

@Serializable
data class PrimaryKeyColumn(private val column: ColumnBase, val keySequence: Int) : ColumnBase by column
