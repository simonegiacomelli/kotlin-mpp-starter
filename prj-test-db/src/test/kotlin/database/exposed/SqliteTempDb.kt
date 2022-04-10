package database.exposed

import Folders
import database.jdbc.JdbcInfo
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.sql.Connection

class SqliteTempDb(private val randomDbName: String = randomDatabaseName()) : TempDbBase {
    init {
        check(randomDbName.lowercase() == randomDbName) {
            "Ho rilevato problemi con Exposed usando nomi di database con lettere maiuscole. Simone"
        }
    }

    private val tempFolder = Folders(File(".")).data.tmp.resolve("sqlite-test-db").canonicalFile

    private val jdbcTest: JdbcInfo by lazy {
        JdbcInfo(
            "sqlite",
            "org.sqlite.JDBC",
            "jdbc:sqlite:{dbname}",
            "",
            ""
        )
    }
    private val jdbcRoot by lazy { jdbcResolve("") }

    private val databaseFile = tempFolder.resolve(randomDbName)
    private val jdbc by lazy { jdbcResolve(databaseFile.canonicalPath) }

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
    override fun create() {
        tempFolder.mkdirs()
    }

    override fun remove() {
        databaseFile.deleteOnExit()
    }
}

