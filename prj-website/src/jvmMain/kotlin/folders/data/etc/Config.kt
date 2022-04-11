package folders.data.etc

import config.booleanProperty
import config.jdbcProperty
import java.util.*

class Config : Properties() {
    val watch_design_html by booleanProperty("watch_design_html", false)
    val database by jdbcProperty("database")
}
