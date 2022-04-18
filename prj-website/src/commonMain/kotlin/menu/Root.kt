package menu

object root : Menu(parent = null, "root", "root") {
    val development = Development(this)
    val accessControl = AccessControl(this)
    val math = Math(this)
    val logoff = menu("logoff", "Log off")
}


