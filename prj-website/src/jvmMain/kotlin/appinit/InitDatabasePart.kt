package appinit

import database.exposed.Exposed
import database.schema.autoCreateTableList
import folders.Folders
import folders.data.etc.config
import folders.data.etc.database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

internal fun Folders.initDatabasePart() {
    waitJdbcInfo()
    config().database()
    transaction {
        SchemaUtils.createMissingTablesAndColumns(*autoCreateTableList.toTypedArray())
    }
    Exposed.logLevelInfo()
}

