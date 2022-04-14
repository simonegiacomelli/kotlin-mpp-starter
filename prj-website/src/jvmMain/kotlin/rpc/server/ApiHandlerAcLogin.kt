package rpc.server

import api.names.ApiAcLoginRequest
import api.names.ApiAcLoginResponse
import security.randomString

private val reg1 = contextHandler.register { req: ApiAcLoginRequest, _ ->
    req.run {
        println("Auth request, user=$username pw=$password")
//        transaction {
//            val user = ac_users.select { ac_users.username eq username }.firstOrNull()
//
//        }
    }

    ApiAcLoginResponse(randomString(30))
}