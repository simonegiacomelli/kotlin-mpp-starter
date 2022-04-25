package database.databinding

import database.exposed.DatabaseTest
import database.schema.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals

class BindToTest : DatabaseTest() {


    private object db_users : Table() {
        val id = integer("id")
        val name = varchar("name", 50)
        val counter = integer("counter")
        override val primaryKey = PrimaryKey(id)
    }

    private data class User(
        var id: Int = 0,
        var name: String = "",
        var counter: Int = 0,
    )

    override fun setupExposed() {
        tables(db_users)
    }

    @Test
    fun test_bind() {
        transaction {
            db_users.insert {
                it[id] = 1
                it[name] = "foo"
                it[counter] = 42
            }
            db_users.insert {
                it[id] = 2
                it[name] = "bar"
                it[counter] = 43
            }
        }
        val mapper = listOf(
            db_users.id bindTo User::id,
            db_users.name bindTo User::name,
            db_users.counter bindTo User::counter
        ).toMapper { User() }
        val users = transaction { db_users.selectAll().map { mapper.map(it) } }
            .sortedBy { it.id }

        assertEquals(listOf(User(1, "foo", 42), User(2, "bar", 43)), users)

    }
}

