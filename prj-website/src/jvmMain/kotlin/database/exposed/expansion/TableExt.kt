package database.exposed.expansion

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

fun Table.date(name: String) = date(name)

fun Table.datetime(name: String, highResolution: Boolean = false): Column<LocalDateTime> =
    registerColumn(name, KotlinxLocalDateTimeColumnType(highResolution = highResolution))
