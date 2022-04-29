package rpc.server

import api.names.AcSession
import api.names.ApiAcUserSessionsRequest
import api.names.ApiAcUserSessionsResponse
import database.databinding.bindTo
import database.databinding.toMapper
import database.schema.ac_sessions
import database.schema.ac_users
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

private val reg1 = contextHandler.register { req: ApiAcUserSessionsRequest, _ ->

    val mapper = listOf(
        ac_sessions.id bindTo AcSession::id,
        ac_sessions.user_id bindTo AcSession::user_id,
        ac_sessions.created_at bindTo AcSession::created_at,
        ac_sessions.updated_at bindTo AcSession::updated_at,
        ac_users.username bindTo AcSession::username,
    ).toMapper { AcSession() }
    mapper.columns.forEach { println("${it.table.tableName}  ${it.name}") }
    val sessions = transaction {
        ac_sessions.join(ac_users, JoinType.LEFT, ac_sessions.user_id, ac_users.id)
            .slice(mapper.columns).selectAll().map { mapper.map(it) }
    }
    ApiAcUserSessionsResponse(sessions)
}
