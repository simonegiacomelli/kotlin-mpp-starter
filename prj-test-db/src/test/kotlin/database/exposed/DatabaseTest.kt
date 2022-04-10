package database.exposed

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import utils.ClearableLazy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class DatabaseTest {

    private val dbClearable = ClearableLazy({ it.remove() }) { SqliteTempDb() }
    protected val db by dbClearable

    @BeforeTest
    fun databaseTestBeforeTest() {
        createDatabase()
        setupExposed()
    }

    open fun createDatabase() {
        db.create()
    }

    abstract fun setupExposed()

    fun tables(vararg tables: Table) {
        println("setupExposed()")
        transaction(db.database) { SchemaUtils.createMissingTablesAndColumns(*tables) }
    }

    @AfterTest
    fun databaseTestAfterTest() {
        dbClearable.clear()
    }

}