package menu

class Settings(parent: Menu) : Menu(parent, "settings", "Settings") {
    val app_settings = menu("app_settings", "App settings")
}