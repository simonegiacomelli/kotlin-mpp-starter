package cli

import api.names.Credential
import appinit.newState

import ktor.startKtor
import rpc.server.userAddRole
import rpc.server.userCreate
import rpc.server.userPasswd

fun cli(vararg arguments: String, init: Cli.() -> Unit) = with(Cli()) {
    init()
    if (arguments.isEmpty()) {
        no_arguments()
        return@with
    }
    val args = arguments.toList()
    fun match(vararg a: String) = args.take(a.size) == a.toList()

    if (match("start")) start()
    if (match("user", "create")) user_create(args[2])
    if (match("user", "passwd")) user_passwd(Credential(args[2], args[3]))
    if (match("user", "add-role")) user_add_role(AddRole(args[2], args[3]))
}

fun Cli.defaultHandlers() {
    no_arguments = { println("No arguments specified.") }
    start = { startKtor() }
    user_create = {
        newState()
        userCreate(it)
    }
    user_passwd = {
        newState()
        userPasswd(it)
    }
    user_add_role = {
        newState()
        it.run { userAddRole(username, roleName) }
    }
}

class AddRole(val username: String, val roleName: String)

class Cli {
    var start = {}
    var no_arguments = {}
    var user_create: (String) -> Unit = {}
    var user_passwd: (Credential) -> Unit = {}
    var user_add_role: (AddRole) -> Unit = {}
}

