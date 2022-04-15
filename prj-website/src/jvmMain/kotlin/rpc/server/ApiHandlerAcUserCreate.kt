package rpc.server

import api.names.ApiAcUserCreateRequest
import api.names.ApiAcUserCreateResponse
import api.names.UserCredential
import database.schema.ac_users
import database.time.nowAtDefault
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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

fun userCreate(username: String): Unit = transaction {
    ac_users.insert {
        it[ac_users.username] = username
        it[email_confirmed] = false
        it[phone_number_confirmed] = false
        it[two_factor_enabled] = false
        it[lockout_enabled] = false
        it[access_failed_count] = 0
        it[created_at] = nowAtDefault()
    }
}

fun userPasswd(credential: UserCredential) = transaction {
    val res = ac_users.update({ ac_users.username eq credential.username }) {
        it[password_hash] = saltedHash(credential.password)
    }
}

fun userExist(username: String) = transaction {
    TODO()
    ac_users.slice(ac_users.id.count()).select(ac_users.username eq username)

}