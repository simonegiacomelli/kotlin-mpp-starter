package database.time

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun nowAtDefault(): LocalDateTime = Clock.System.now().toLocalDateTime(timeZone = TimeZone.UTC)