package rpc.server

import api.names.AcUser
import api.names.ApiAcUserListRequest
import api.names.ApiAcUserListResponse
import database.databinding.exposedMapper
import database.schema.ac_users
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

private val reg1 = contextHandler.register { req: ApiAcUserListRequest, _ ->

    val mapper = exposedMapper { AcUser() }.bindTo(ac_users)
    val users = transaction { ac_users.slice(mapper.columns()).selectAll().map { mapper.map(it) } }
    ApiAcUserListResponse(users)
}
