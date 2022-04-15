package cli

import api.names.UserCredential
import appinit.initDatabasePart
import folders.folders
import ktor.startKtor
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
    if (match("user", "passwd")) user_passwd(UserCredential(args[2], args[3]))
}

fun Cli.defaultHandlers() {
    no_arguments = { println("No arguments specified.") }
    start = { startKtor() }
    user_create = { minimalStartup(); userCreate(it) }
    user_passwd = { minimalStartup(); userPasswd(it) }
}

class Cli {
    var start = {}
    var no_arguments = {}
    var user_create: (String) -> Unit = {}
    var user_passwd: (UserCredential) -> Unit = {}
}

private fun minimalStartup() {
    folders().initDatabasePart()
}