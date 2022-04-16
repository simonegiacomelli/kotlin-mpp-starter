package accesscontrol

import context.Anonymous
import context.User
import database.schema.ac_sessions
import database.schema.ac_users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Database.acUserFromSession(sessionId: String): User = transaction {
    val session = ac_sessions.slice(ac_sessions.user_id)
        .select { ac_sessions.id eq sessionId }
        .firstOrNull() ?: return@transaction Anonymous
    val user_id = session[ac_sessions.user_id]
    val user = ac_users.slice(ac_users.username, ac_users.email)
        .select { ac_users.id eq user_id }
        .firstOrNull() ?: return@transaction Anonymous

    object : User {
        override val id = user_id
        override val username = user[ac_users.username]
        override val email = user[ac_users.email] ?: ""
    }
}