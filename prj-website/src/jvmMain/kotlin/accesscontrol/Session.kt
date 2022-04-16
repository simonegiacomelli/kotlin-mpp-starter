package accesscontrol

import appinit.State
import database.schema.ac_sessions
import database.schema.ac_users
import database.time.nowAtDefault
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Database.acUserBySessionId(sessionId: String): UserAbs? = transaction(this) {
    val session = ac_sessions.slice(ac_sessions.user_id)
        .select { ac_sessions.id eq sessionId }
        .firstOrNull() ?: return@transaction null
    val user_id = session[ac_sessions.user_id]
    val user = ac_users.slice(ac_users.username, ac_users.email)
        .select { ac_users.id eq user_id }
        .firstOrNull() ?: return@transaction null

    object : UserAbs {
        override val id = user_id
        override val username = user[ac_users.username]
        override val email = user[ac_users.email] ?: ""
    }
}

fun Database.acRefreshSession(sessionId: String) = transaction(this) {
    ac_sessions.update({ ac_sessions.id eq sessionId }) {
        it[updated_at] = nowAtDefault()
    }
}


fun State.acGetUserAndRefresh(sessionId: String): UserAbs? = database.run {
    return acUserBySessionId(sessionId)?.also { acRefreshSession(sessionId) }
}