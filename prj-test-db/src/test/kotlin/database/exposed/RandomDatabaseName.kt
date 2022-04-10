package database.exposed

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun randomDatabaseName(): String {
    val alphabet: List<Char> = ('a'..'z') + ('0'..'9')
    val randomDbName: String = "temp_${
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
            .replace("-", "")
            .replace(":", "")
            .replace(".", "")
            .replace("T", "_")
    }_" + List(4) { alphabet.random() }.joinToString("")
    return randomDbName
}