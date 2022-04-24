package jdbcx

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass


val jdbcColumnModule = SerializersModule {
    polymorphic(ColumnBase::class) {
        subclass(ColumnBaseDc::class)
    }
    polymorphic(Column::class) {
        subclass(IntColumn::class)
        subclass(LongColumn::class)
        subclass(DatetimeColumn::class)
        subclass(DateColumn::class)
        subclass(TimeColumn::class)
        subclass(DoubleColumn::class)
        subclass(BigintColumn::class)
        subclass(BitColumn::class)
        subclass(LongtextColumn::class)
        subclass(VarbinaryColumn::class)
        subclass(VarcharColumn::class)
    }
}