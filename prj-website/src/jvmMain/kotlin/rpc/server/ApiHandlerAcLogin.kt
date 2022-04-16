package rpc.server

import accesscontrol.acUserFromSession
import api.names.ApiAcLoginRequest
import api.names.ApiAcSession
import api.names.ApiAcSessionResponse
import api.names.Credential
import context.toDataclass
import database.schema.ac_sessions
import database.schema.ac_users
import database.schema.session_id_length
import database.time.nowAtDefault
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import security.randomString
import security.verifySaltedHash

private val reg1 = contextHandler.register { req: ApiAcLoginRequest, context ->
    val session_id = req.credential.newSessionIfValid()
        ?: return@register ApiAcSessionResponse(null)
    val user = context.database.acUserFromSession(session_id)
    ApiAcSessionResponse(ApiAcSession(session_id, user.toDataclass()))
}

private fun Credential.newSessionIfValid(): String? = transaction {
    println("Auth request, user=$username pw=$password")
    val user = ac_users.select { ac_users.username eq username }.firstOrNull()
    user.newSessionIfValid(password)
}

private fun ResultRow?.newSessionIfValid(password: String): String? {
    if (this == null) return null
    val hash = this[ac_users.password_hash] ?: return null
    if (hash.isBlank() || !verifySaltedHash(hash, password)) return null
    val user_id = this[ac_users.id].value

    val session_id = randomString(session_id_length)

    ac_sessions.insert {
        it[this.id] = session_id
        it[this.user_id] = user_id
        val now = nowAtDefault()
        it[created_at] = now
        it[updated_at] = now
    }
    return session_id
}
