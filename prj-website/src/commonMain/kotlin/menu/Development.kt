package menu

import accesscontrol.Role.Admin

class Development(parent: Menu) : Menu(parent, "development", "Development", Admin) {
    val html_editor = menu("html_editor", "Html editor")
    val html_display = menu("html_display", "Html display")
}