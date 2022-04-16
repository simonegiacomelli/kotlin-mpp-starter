package rpc.server

import accesscontrol.Role
import api.names.ApiAcUserCreateRequest
import api.names.Credential
import database.schema.ac_user_roles
import database.schema.ac_users
import database.time.nowAtDefault
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import rpc.VoidResponse
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

    VoidResponse
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

fun userPasswd(credential: Credential) = transaction {
    val res = ac_users.update({ ac_users.username eq credential.username }) {
        it[password_hash] = saltedHash(credential.password)
    }
}

fun userAddRole(username: String, roleName: String) = transaction {
    val map = Role.values().associate { it.name to it.id }
    val role_id = map[roleName]
        ?: error("Role `${roleName}` not found. Available roles:\n" + map.keys.joinToString("\n") { "  $it" })
    val user_id = ac_users.slice(ac_users.id)
        .select { ac_users.username eq username }
        .firstOrNull()?.let { it[ac_users.id].value }
        ?: error("User `$username` not found")

    ac_user_roles.insert {
        it[ac_user_roles.user_id] = user_id
        it[ac_user_roles.role_id] = role_id
    }

}

fun userExist(username: String) = transaction {
    TODO()
    ac_users.slice(ac_users.id.count()).select(ac_users.username eq username)

}