package rpc.server

import accesscontrol.Role
import api.names.*
import database.schema.ac_user_roles
import database.schema.ac_users
import database.time.nowAtDefault
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import security.saltedHash

private val reg1 = contextHandler.register { req: ApiAcUserCreateRequest, _ ->
    val msg = req.run {
        println("Auth create request, user=$username pw=$password")
        val exists = transaction { ac_users.select { ac_users.username eq username }.count() >= 1 }
        if (exists) "User $username already exists!"
        else {
            userCreate(username)
            userPasswd(Credential(username, password))
            "User $username created successfully"
        }
    }

    ApiAcUserCreateResponse(msg)
}
private val reg2 = contextHandler.register { req: ApiAcUserPasswdRequest, _ ->
    val msg = req.run {
        println("Auth create request, user=$username pw=$password")
        val exists = transaction { ac_users.select { ac_users.username eq username }.count() >= 1 }
        if (!exists) "User $username do not exists!"
        else {
            userPasswd(Credential(username, password))
            "User $username password set successfully"
        }
    }

    ApiAcUserPasswdResponse(msg)
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

class UserRoleStr(val username: String, val roleName: String)
class UserRoleInt(val user_id: Int, val role_id: Int)

fun UserRoleStr.toInt() = run {
    val map = Role.values().associate { it.name to it.id }
    val role_id = map[roleName]
        ?: error("Role `${roleName}` not found. Available roles:\n" + map.keys.joinToString("\n") { "  $it" })
    val user_id = ac_users.slice(ac_users.id)
        .select { ac_users.username eq username }
        .firstOrNull()?.let { it[ac_users.id].value }
        ?: error("User `$username` not found")
    UserRoleInt(user_id, role_id)
}

fun userAddRole(userRole: UserRoleStr) = transaction { userAddRole(userRole.toInt()) }
fun userAddRole(userRole: UserRoleInt) {
    ac_user_roles.insert {
        it[user_id] = userRole.user_id
        it[role_id] = userRole.role_id
    }
}

// todo should invalidate session (delete all records in ac_sessions for user_id) ?
fun userRemoveRole(userRole: UserRoleStr) = transaction { userRemoveRole(userRole.toInt()) }
fun userRemoveRole(userRole: UserRoleInt) {
    ac_user_roles.deleteWhere {
        (ac_user_roles.user_id eq userRole.user_id) and
                (ac_user_roles.role_id eq userRole.role_id)
    }
}

fun userExist(username: String) = transaction {
    TODO()
    ac_users.slice(ac_users.id.count()).select(ac_users.username eq username)

}