package database.exposed

import folders.FoldersFinder
import folders.data.etc.config
import jdbc.Jdbc
import org.jetbrains.exposed.sql.Database
import java.sql.Connection

class PgsqlTempDb(private val randomDbName: String = randomDatabaseName()) : TempDbBase {
    init {
        check(randomDbName.lowercase() == randomDbName) {
            "Ho rilevato problemi con Exposed usando nomi di database con lettere maiuscole. Simone"
        }
    }

    private val jdbcTest: Jdbc by lazy {
        FoldersFinder().folders.config().databaseTest.apply {
            if (url == "")
                error(
                    "Per eseguire i test sul database e' necessario specificare il database 'database.Test' nella configurazione\n\n" +
                            "Esempio:\n" +
                            "database.Test/driver=org.postgresql.Driver\n" +
                            "database.Test/url=jdbc:postgresql://localhost:15432/{dbname}\n" +
                            "database.Test/user=foo\n" +
                            "database.Test/password=bar\n"
                )
        }
    }
    private val jdbcRoot by lazy { jdbcResolve("postgres") }

    private val jdbc by lazy { jdbcResolve(randomDbName) }

    private fun jdbcResolve(dbName: String): Jdbc {
        return jdbcTest.copy(url = jdbcTest.url.replace("{dbname}", dbName))
    }

    override fun connection(): Connection = jdbc.newConnection()

    override val database: Database by lazy {
        jdbc.run { Database.connect(url, driver = driver, user = user, password = password) }
    }

    override fun create() {
        // "CREATE DATABASE db WITH OWNER = postgres ENCODING = 'UTF8' TABLESPACE = pg_default LC_COLLATE = 'C' LC_CTYPE = 'C' CONNECTION LIMIT = -1;"
        jdbcRoot.newConnection().use { it.prepareStatement("CREATE DATABASE $randomDbName").execute() }
    }

    override fun remove() {
        jdbcRoot.newConnection().use { it.prepareStatement("DROP DATABASE $randomDbName").execute() }
    }
}
