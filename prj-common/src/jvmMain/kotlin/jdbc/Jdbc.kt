package jdbc

import java.sql.Connection
import java.sql.DriverManager
import java.util.*

data class Jdbc(val type: String, val driver: String, val url: String, val user: String, val password: String) {

    companion object {
        fun load(props: Properties, propertyPrefix: String): Jdbc {
            fun prop(suffix: String) = props.getProperty("$propertyPrefix/$suffix", "")
            return Jdbc(
                type = prop("type"),
                driver = prop("driver"),
                url = prop("url"),
                user = prop("user"),
                password = prop("password"),
            )
        }
    }

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