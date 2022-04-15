package cli

import ktor.startKtor

fun cli(vararg arguments: String, init: Cli.() -> Unit) = with(Cli()) {
    init()
    val args = arguments.toList()
    fun match(vararg a: String) = args.take(a.size) == a.toList()

    if (match("start")) start()
    if (match("user", "create")) user_create(arguments[2])
    if (match("user", "passwd")) user_passwd(arguments[2])
}

fun Cli.defaultHandlers() {
    start = { startKtor() }
}

class Cli {
    var start = {}
    var user_create: (String) -> Unit = {}
    var user_passwd: (String) -> Unit = {}
}