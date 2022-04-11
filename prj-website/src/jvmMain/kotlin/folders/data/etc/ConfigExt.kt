package folders.data.etc

import org.jetbrains.exposed.sql.Database

fun Config.database(): Database = database.run {
    Database.connect(url, driver = driver, user = user, password = password)
}
