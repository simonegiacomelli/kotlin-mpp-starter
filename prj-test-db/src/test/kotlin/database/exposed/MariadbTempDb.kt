package database.exposed

import jdbc.Jdbc
import org.jetbrains.exposed.sql.Database
import java.sql.Connection

class MariadbTempDb(private val randomDbName: String = randomDatabaseName()) : TempDbBase {
    init {
        check(randomDbName.lowercase() == randomDbName) {
            "Ho rilevato problemi con Exposed usando nomi di database con lettere maiuscole. Simone"
        }
    }

    private val jdbcTest: Jdbc by lazy {
        Jdbc("sqlite", "org.sqlite.JDBC", "jdbc:sqlite:{dbname}?mode=memory&cache=shared", "", "")
    }
    private val jdbcRoot by lazy { jdbcResolve("") }

    private val jdbc by lazy { jdbcResolve(randomDbName) }

    private fun jdbcResolve(dbName: String): Jdbc {
        return jdbcTest.copy(url = jdbcTest.url.replace("{dbname}", dbName))
    }

    override fun connection(): Connection = jdbc.newConnection()

    override val database: Database by lazy {
        val i = jdbc
        val db = Database.connect(i.url, driver = i.driver, user = i.user, password = i.password)
        db
    }

    override fun create() {
        jdbcRoot.newConnection().use { it.prepareStatement("CREATE DATABASE `$randomDbName`").execute() }
    }

    override fun remove() {
        jdbcRoot.newConnection().use { it.prepareStatement("DROP DATABASE `$randomDbName`").execute() }
    }
}
