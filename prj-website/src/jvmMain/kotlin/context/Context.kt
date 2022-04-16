package context

import org.jetbrains.exposed.sql.Database

interface Context {
    val database: Database
    val user: User
}