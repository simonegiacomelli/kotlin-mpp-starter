package rpc.server

import api.names.ApiAcUserListRequest
import api.names.ApiAcUserListResponse

private val reg1 = contextHandler.register { req: ApiAcUserListRequest, _ ->

//    transaction {
//        val m = mapper1(fieldMap.keys.toList())
//
//        ac_users.selectAll().map { m(it, AcUser()) }
//    }
    ApiAcUserListResponse(emptyList())
}
