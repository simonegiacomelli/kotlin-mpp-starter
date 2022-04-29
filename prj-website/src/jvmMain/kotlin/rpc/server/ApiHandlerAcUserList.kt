package rpc.server

import api.names.AcUser
import api.names.ApiAcUserListRequest
import api.names.ApiAcUserListResponse
import database.databinding.bindTo
import database.databinding.toMapper
import database.schema.ac_users
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

private val reg1 = contextHandler.register { req: ApiAcUserListRequest, _ ->

    val mapper = listOf(
        ac_users.id bindTo AcUser::id,
        ac_users.username bindTo AcUser::username,
        ac_users.email bindTo AcUser::email,
        ac_users.phone_number bindTo AcUser::phone_number,
        ac_users.lockout_end_date_utc bindTo AcUser::lockout_end_date_utc,
        ac_users.lockout_enabled bindTo AcUser::lockout_enabled,
        ac_users.access_failed_count bindTo AcUser::access_failed_count
    ).toMapper { AcUser() }
    val users = transaction { ac_users.slice(mapper.columns()).selectAll().map { mapper.map(it) } }
    ApiAcUserListResponse(users)
}
