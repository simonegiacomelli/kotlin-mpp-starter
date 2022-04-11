package database.exposed

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import utils.ClearableLazy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class DatabaseTest {

    private val dbClearable = ClearableLazy({ it.remove() }) { PgsqlTempDb() }
    protected val temp by dbClearable

    @BeforeTest
    fun databaseTestBeforeTest() {
        createDatabase()
        setupExposed()
    }

    open fun createDatabase() {
        temp.create()
    }

    abstract fun setupExposed()

    fun tables(vararg tables: Table) {
        println("setupExposed()")
        transaction(temp.database) { SchemaUtils.createMissingTablesAndColumns(*tables) }
    }

    @AfterTest
    fun databaseTestAfterTest() {
        dbClearable.clear()
    }

}