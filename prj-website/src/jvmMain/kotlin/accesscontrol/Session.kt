package accesscontrol

import appinit.State
import database.schema.ac_sessions
import database.schema.ac_user_roles
import database.schema.ac_users
import database.time.nowAtDefault
import kotlinx.serialization.Serializable
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

    val roleIds = ac_user_roles.slice(ac_user_roles.role_id)
        .select { ac_user_roles.user_id eq user_id }
        .map { it[ac_user_roles.role_id] }
        .map { RoleInt(it) }
        .toSet()

    object : UserAbs {
        override val id = user_id
        override val username = user[ac_users.username]
        override val email = user[ac_users.email] ?: ""
        override val roles: Set<RoleAbs> = roleIds
    }
}

@Serializable
data class RoleInt(override val id: Int) : RoleAbs

fun Database.acRefreshSession(sessionId: String) = transaction(this) {
    ac_sessions.update({ ac_sessions.id eq sessionId }) {
        it[updated_at] = nowAtDefault()
    }
}


fun State.acGetUserAndRefresh(sessionId: String): UserAbs? = database.run {
    return acUserBySessionId(sessionId)?.also { acRefreshSession(sessionId) }
}