package context

import accesscontrol.UserAbs
import org.jetbrains.exposed.sql.Database

interface Context {
    val database: Database
    val user: UserAbs
}