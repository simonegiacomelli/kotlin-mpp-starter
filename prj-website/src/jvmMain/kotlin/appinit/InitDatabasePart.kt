package appinit

import database.exposed.Exposed
import database.schema.autoCreateTableList
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

internal fun AppInit.initDatabasePart() {

    transaction {
        SchemaUtils.createMissingTablesAndColumns(*autoCreateTableList.toTypedArray())
    }
    Exposed.logLevelInfo()
}

