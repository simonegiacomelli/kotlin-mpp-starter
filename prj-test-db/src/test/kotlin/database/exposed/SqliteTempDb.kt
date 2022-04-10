package database.exposed

import database.jdbc.JdbcInfo
import org.jetbrains.exposed.sql.Database
import java.sql.Connection

class SqliteTempDb(private val randomDbName: String = randomDatabaseName()) : TempDbBase {
    init {
        check(randomDbName.lowercase() == randomDbName) {
            "Ho rilevato problemi con Exposed usando nomi di database con lettere maiuscole. Simone"
        }
    }

    private val jdbcTest: JdbcInfo by lazy {
        JdbcInfo("sqlite", "org.sqlite.JDBC", "jdbc:sqlite:{dbname}?mode=memory&cache=shared", "", "")
    }
    private val jdbcRoot by lazy { jdbcResolve("") }

    private val jdbc by lazy { jdbcResolve(randomDbName) }

    private fun jdbcResolve(dbName: String): JdbcInfo {
        return jdbcTest.copy(url = jdbcTest.url.replace("{dbname}", dbName))
    }

    override fun connection(): Connection = jdbc.newConnection()

    override val database: Database by lazy {
        val i = jdbc
        val db = Database.connect(i.url, driver = i.driver, user = i.user, password = i.password)
        db
    }

    // todo https://www.sqlite.org/inmemorydb.html
    override fun create() = run {}

    override fun remove() = run {}
}

