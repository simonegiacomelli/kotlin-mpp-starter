package rpc.server

import api.names.AcSession
import api.names.ApiAcUserSessionsRequest
import api.names.ApiAcUserSessionsResponse
import database.databinding.exposedMapper
import database.schema.ac_sessions
import database.schema.ac_users
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

private val reg1 = contextHandler.register { req: ApiAcUserSessionsRequest, _ ->

    val mapper = exposedMapper { AcSession() }
        .bindTo(ac_sessions)
        .bindTo(ac_users)

    val sessions = transaction {
        ac_sessions.join(ac_users, JoinType.LEFT, ac_sessions.user_id, ac_users.id)
            .slice(mapper.columns).selectAll().map { mapper.map(it) }
    }
    ApiAcUserSessionsResponse(sessions)
}
