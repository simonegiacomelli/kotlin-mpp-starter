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
import telemetry.newEvent

private val nullSession = ApiAcSessionResponse(null)

private val reg1 = contextHandler.register { req: ApiAcLoginRequest, context ->
    val maybeSessionId = transaction { req.newSessionIfValid() }
    logOk(req.credential, maybeSessionId)
    if (maybeSessionId == null)
        nullSession
    else
        context.database.getApiSessionForId(maybeSessionId)
}

fun logOk(credential: Credential, maybeSessionId: String?) = credential.run {
    println("Auth request, user=$username pw=$password ok=${maybeSessionId != null}")
}

private val reg2 = contextHandler.register { req: ApiAcVerifySessionRequest, context ->
    val response = context.database.getApiSessionForId(req.id)
    newEvent(context.database, 2, "restoreSession=${response.session != null}")
    response
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

private fun ApiAcLoginRequest.newSessionIfValid(): String? {
    val username = credential.username
    val pw = credential.password
    val user = ac_users.select { ac_users.username eq username }.firstOrNull() ?: return null
    val hash = user[ac_users.password_hash] ?: return null
    if (hash.isBlank() || !verifySaltedHash(hash, pw)) return null
    val userId = user[ac_users.id].value

    val sessionId = randomString(session_id_length)

    ac_sessions.insert {
        it[id] = sessionId
        it[user_id] = userId
        it[screen] = client.screen
        it[platform] = client.platform
        it[user_agent] = client.userAgent
        val now = nowAtDefault()
        it[created_at] = now
        it[updated_at] = now
    }

    return sessionId
}
