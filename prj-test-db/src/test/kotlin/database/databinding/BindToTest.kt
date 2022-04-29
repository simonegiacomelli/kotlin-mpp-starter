package database.databinding

import database.exposed.DatabaseTest
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
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
            db_usersInsert(1, "foo", 42)
            db_usersInsert(2, "bar", 43)
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

    private fun db_usersInsert(i: Int, s: String, i1: Int) {
        db_users.insert {
            it[id] = i
            it[name] = s
            it[counter] = i1
        }
    }


    object db_users_autopk : IntIdTable() {
        val name = varchar("name", 50)
        val counter = integer("counter")
    }

    @Test
    fun test_bind_autoPrimaryKey() {
        val fooId = db_users_autopk_Insert("foo", 42)
        val barId = db_users_autopk_Insert("bar", 43)

        val mapper = listOf(
            db_users_autopk.id bindTo User::id,
            db_users_autopk.name bindTo User::name,
            db_users_autopk.counter bindTo User::counter
        ).toMapper { User() }
        val users = transaction { db_users_autopk.selectAll().map { mapper.map(it) } }
            .sortedBy { it.id }

        assertEquals(listOf(User(fooId, "foo", 42), User(barId, "bar", 43)), users)

    }

    private fun db_users_autopk_Insert(s: String, i: Int) = transaction {
        (db_users_autopk.insert {
            it[name] = s
            it[counter] = i
        } get (db_users_autopk.id)).value
    }

    @Test
    fun test_bindToSerializable() {
        @Serializable
        data class SUser(
            var id: Int = 0,
            var name: String = "",
            var counter: Int = 0,
        )

//        exposedMapper { SUser() }.mapAll(db_users)
    }
}

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> exposedMapper(constructor: () -> T) {
//    val s = T::class.
//    s.descriptor.elementNames
}

