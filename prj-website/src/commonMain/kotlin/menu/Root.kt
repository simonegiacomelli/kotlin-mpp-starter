package menu

object root : Menu(parent = null, "root", "root") {
    val accessControl = AccessControl(this)
    val math = Math(this)
    val development = Development(this)
    val logoff = menu("logoff", "Log off")
}


