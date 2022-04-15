package rpc.server

import api.names.ApiAcLoginRequest
import api.names.ApiAcLoginResponse
import database.schema.ac_users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import security.randomString
import security.verifySaltedHash

private val reg1 = contextHandler.register { req: ApiAcLoginRequest, _ ->
    ApiAcLoginResponse(req.getSessionIfValid())
}

private fun ApiAcLoginRequest.getSessionIfValid(): String = transaction {
    println("Auth request, user=$username pw=$password")
    val user = ac_users.select { ac_users.username eq username }.firstOrNull()
    if (user.credentialValid(password)) randomString(30) else ""
}

private fun ResultRow?.credentialValid(password: String): Boolean {
    if (this == null) return false
    val hash = this[ac_users.password_hash] ?: ""
    if (hash.isBlank()) return false
    return verifySaltedHash(hash, password)
}
