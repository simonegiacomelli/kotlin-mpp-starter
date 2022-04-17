package rpc.server

import accesscontrol.Anonymous
import accesscontrol.Session
import accesscontrol.acUserBySessionId
import accesscontrol.toDataclass
import api.names.*
import database.schema.ac_sessions
import database.schema.ac_users
import database.schema.session_id_length
import database.time.nowAtDefault
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import rpc.VoidResponse
import security.randomString
import security.verifySaltedHash

private val nullSession = ApiAcSessionResponse(null)

private val reg1 = contextHandler.register { req: ApiAcLoginRequest, context ->
    val sessionId = req.credential.newSessionIfValid() ?: return@register nullSession
    context.database.getApiSessionForId(sessionId)
}

private val reg2 = contextHandler.register { req: ApiAcVerifySessionRequest, context ->
    context.database.getApiSessionForId(req.id)
}

private val reg3 = contextHandler.register { req: ApiAcLogoffRequest, context ->
    val sessionId = req.id
    val user = context.user
    if (user !is Anonymous) transaction {
        ac_sessions.deleteWhere { (ac_sessions.id eq sessionId) and (ac_sessions.user_id eq user.id) }
    }
    VoidResponse
}

private fun Database.getApiSessionForId(sessionId: String): ApiAcSessionResponse {
    val user = acUserBySessionId(sessionId) ?: return nullSession
    return ApiAcSessionResponse(Session(sessionId, user.toDataclass()))
}

private fun Credential.newSessionIfValid(): String? = transaction {
    val user = ac_users.select { ac_users.username eq username }.firstOrNull()
    user.newSessionIfValid(password).also {
        println("Auth request, user=$username pw=$password ok=${it != null}")
    }
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
