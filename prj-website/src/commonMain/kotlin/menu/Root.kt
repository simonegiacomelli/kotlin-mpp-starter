package menu

object root : Menu(parent = null, "root", "root") {
    val accessControl = AccessControl(this)
    val development = Development(this)
}

