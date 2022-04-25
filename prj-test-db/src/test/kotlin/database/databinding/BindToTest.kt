package database.databinding

import database.exposed.DatabaseTest
import database.schema.IntIdTable
import database.schema.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals

class BindToTest : DatabaseTest() {


    override fun setupExposed() {
        tables(db_users, db_users_autopk)
    }

    object db_users : Table() {
        val id = integer("id")
        val name = varchar("name", 50)
        val counter = integer("counter")
        override val primaryKey = PrimaryKey(id)
    }

    data class User(
        var id: Int = 0,
        var name: String = "",
        var counter: Int = 0,
    )

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


    object db_users_autopk : IntIdTable() {
        val name = varchar("name", 50)
        val counter = integer("counter")
    }

    @Test
    fun test_bind_autoPrimaryKey() {

        val fooId = transaction {
            (db_users_autopk.insert {
                it[name] = "foo"
                it[counter] = 42
            } get (db_users_autopk.id)).value
        }
        val barId = transaction {
            (db_users_autopk.insert {
                it[name] = "bar"
                it[counter] = 43
            } get db_users_autopk.id).value

        }
        val mapper = listOf(
            db_users_autopk.id bindTo User::id,
            db_users_autopk.name bindTo User::name,
            db_users_autopk.counter bindTo User::counter
        ).toMapper { User() }
        val users = transaction { db_users_autopk.selectAll().map { mapper.map(it) } }
            .sortedBy { it.id }

        assertEquals(listOf(User(fooId, "foo", 42), User(barId, "bar", 43)), users)

    }
}

