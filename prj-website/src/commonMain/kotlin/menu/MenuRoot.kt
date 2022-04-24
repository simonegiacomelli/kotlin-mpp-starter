package menu

object menuRoot : Menu(parent = null, "root", "root") {
    val development = Development(this)
    val accessControl = AccessControl(this)
    val telemetry = Telemetry(this)
    val math = Math(this)
    val logoff = menu("logoff", "Log off")
}


