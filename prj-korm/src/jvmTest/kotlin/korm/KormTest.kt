package korm

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class KormTest {
    private var connection = DriverManager.getConnection("jdbc:sqlite::memory:")

    @BeforeTest
    fun beforeTest() {
        Class.forName("org.sqlite.JDBC")
        connection = DriverManager.getConnection("jdbc:sqlite::memory:")
        connection.update("create table users(id integer, name varchar(20), age integer, primary key (id))")
    }


    data class User(val id: Int, val name: String?, val age: Int)

    @Test
    fun test_query() {

        connection.update("insert into users values (1, 'foo', 42)")
        connection.update("insert into users values (2, null, 43)")

        val rs2: ResultSet = connection.prepareStatement("select id,name,age from users").executeQuery()!!

        val user2 = rs2.korm(::User)

        assertEquals(listOf(User(1, "foo", 42), User(2, null, 43)), user2.toList())
    }

    @Test
    fun test_insert() {

        val meta = Meta(
            tableName = "users",
            primaryKey = User::id,
            fields = listOf(User::name, User::age)
        )

        val userFoo = User(1, "foo", 42)
        val userBar = User(2, "bar", 43)
        connection.kormInsert(meta, userFoo, userBar)


        val rs2: ResultSet = connection.prepareStatement("select id,name,age from users order by id").executeQuery()!!

        val actualRows = rs2.korm(::User).toList()
        assertEquals(actualRows, listOf(userFoo, userBar))
    }

    private fun Connection.update(sql: String) = prepareStatement(sql).executeUpdate()

}
