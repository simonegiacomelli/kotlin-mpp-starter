package database.jdbc

import java.sql.Connection
import java.sql.DriverManager

data class JdbcInfo(val type: String, val driver: String, val url: String, val user: String, val password: String) {
    fun newConnection(): Connection = DriverManager.getConnection(url, user, password)

    fun registerDriver() {
        try {
            Class.forName(driver)
        } catch (e: ClassNotFoundException) {
            throw DriverNotFoundException(driver)
        }
    }
}

class DriverNotFoundException(driver: String?) : RuntimeException(driver)