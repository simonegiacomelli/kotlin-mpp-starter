package telemetry

import database.exposed.DatabaseTest
import database.schema.tm_events
import database.schema.tm_types
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals

class EventTest : DatabaseTest() {

    override fun setupExposed() {
        tables(tm_events, tm_types)
    }

    @Test
    fun test_new_event() = transaction {
        // GIVEN
        val now = "2010-06-01T22:19:44".toLocalDateTime()

        // WHEN
        newEvent(temp.database, 42, "arg1", created_at = now)

        // THEN
        val rows = tm_events.run { selectAll().map { listOf<Any>(it[type_id], it[created_at], it[arguments]) } }
        assertEquals(listOf(listOf(42, now, "arg1")), rows)
    }

    @Test
    fun test_new_event_with_type_as_string() = transaction {
        // GIVEN
        data class ET(override val id: Int, override val name: String) : EventType

        val et42 = ET(42, "type42")
        val et56 = ET(56, "type56")

        // WHEN
        newEvent(temp.database, et42)
        newEvent(temp.database, et56)
        newEvent(temp.database, et42)

        // THEN
        val events = tm_events.run { selectAll().sortedBy { it[id] }.map { it[type_id] } }
        assertEquals(listOf(42, 56, 42), events)

        val types = tm_types.run { selectAll().sortedBy { it[id] }.map { ET(it[id], it[name]) } }
        assertEquals(listOf(et42, et56), types)
    }
}