package korm

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.reflect.KFunction2
import kotlin.reflect.*
import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.assertEquals


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

    @Test
    fun test_insert() {
        Class.forName("org.sqlite.JDBC")
        val connection = DriverManager.getConnection("jdbc:sqlite::memory:")

        connection.prepareStatement("create table users(id integer, name varchar(20), age integer, primary key (id))")
            .executeUpdate()


        data class User2(val id: Int, val name: String?, val age: Int)

        val meta = Meta(
            tableName = "users",
            primaryKey = User2::id,
            fields = listOf(User2::name, User2::age)
        )

        val userFoo = User2(1, "foo", 42)
        val userBar = User2(2, "bar", 43)
        connection.kormInsert(meta, userFoo, userBar)


        val rs2: ResultSet = connection.prepareStatement("select id,name,age from users order by id").executeQuery()!!

        val actualRows = rs2.korm(::User2).toList()
        assertEquals(actualRows, listOf(userFoo, userBar))
    }


    @JvmName("korm2")
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

    @JvmName("korm3")
    private inline fun <reified A, reified B, reified C, reified Res> ResultSet.korm(
        kFun: KFunction3<A, B, C, Res>
    ): Sequence<Res> = sequence {

        while (next()) {
            val a = getObject(1) as A
            val b = getObject(2) as B
            val c = getObject(3) as C
            val value: Res = kFun(a, b, c)
            yield(value)
        }
    }


}

class Meta<T>(val tableName: String, val primaryKey: KProperty1<T, *>, val fields: List<KProperty1<T, *>>)

private fun <T> Connection.kormInsert(meta: Meta<T>, vararg rows: T) = meta.run {
    val fieldList = buildList { add(primaryKey); addAll(fields) }
    val fieldNameList = fieldList.joinToString(", ") { it.name }
    val argsList = fieldList.joinToString(", ") { "?" }
    val statement = prepareStatement("insert into $tableName ($fieldNameList) values ($argsList) ")
    rows.forEach { row ->
        val values = fieldList.map { it.get(row) }
        values.forEachIndexed { index, any -> statement.setObject(index + 1, any) }
        statement.executeUpdate()
    }
    statement.close()
}
