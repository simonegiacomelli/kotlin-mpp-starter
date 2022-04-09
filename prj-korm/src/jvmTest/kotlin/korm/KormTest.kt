package korm

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.reflect.KFunction2
import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail


class KormTest {

    @Test
    fun test_query() {
        Class.forName("org.sqlite.JDBC")
        val connection = DriverManager.getConnection("jdbc:sqlite::memory:")

        connection.prepareStatement("create table users(id integer, name varchar(20), age integer, primary key (id))")
            .executeUpdate()
        connection.prepareStatement("insert into users values (1,'foo',42)").executeUpdate()
        connection.prepareStatement("insert into users values (2,null,43)").executeUpdate()

        val rs2: ResultSet = connection.prepareStatement("select id,name from users").executeQuery()!!


        data class User2(val id: Int, val name: String?)

        val user2 = rs2.korm(::User2)
        assertEquals(listOf(User2(1, "foo"), User2(2, null)), user2.toList())


    }

    //    @Test()
    fun test_insert() {
        Class.forName("org.sqlite.JDBC")
        val connection = DriverManager.getConnection("jdbc:sqlite::memory:")

        connection.prepareStatement("create table users(id integer, name varchar(20), age integer, primary key (id))")
            .executeUpdate()


        data class User2(val id: Int, val name: String?)

        val meta = Meta(
            primaryKey = User2::id,
            fields = listOf(User2::name)
        )
        meta.primaryKey.name

        val user2 = User2(1, "foo")
        connection.kormInsert(meta, user2)


        val rs2: ResultSet = connection.prepareStatement("select id,name from users").executeQuery()!!

        val user2Actual = rs2.korm(::User2).toList()
        assertEquals(user2Actual, listOf(user2))

        fail("track me!!!")
    }


    private inline fun <reified A, reified B, reified Res> ResultSet.korm(
        kFun: KFunction2<A, B, Res>
    ): Sequence<Res> = sequence {

        while (next()) {
            val a = getObject(1) as A
            val b = getObject(2) as B
            val value: Res = kFun(a, b)
            yield(value)
        }
    }


}

class Meta<T>(val primaryKey: KProperty1<T, *>, val fields: List<KProperty1<T, *>>)

private fun <T> Connection.kormInsert(meta: Meta<T>, user2: T) {
    val primaryKeyName = meta.primaryKey.name

}
