package rpc.server

import api.names.ApiAcUserCreateRequest
import api.names.ApiAcUserCreateResponse
import database.schema.ac_users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import security.saltedHash

private val reg1 = contextHandler.register { req: ApiAcUserCreateRequest, _ ->
    req.run {
        println("Auth create request, user=$username pw=$password")
    }
    transaction {
        ac_users.insert {
            it[username] = req.username
            it[password_hash] = saltedHash(req.password)
        }
    }


    ApiAcUserCreateResponse()
}