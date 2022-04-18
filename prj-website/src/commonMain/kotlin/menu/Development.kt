package menu

import accesscontrol.Role.Admin

class Development(parent: Menu) : Menu(parent, "development", "Development", Admin) {
    val spinner = menu("spinner3", "Spinner for 3 secs")
    val html_editor = menu("html_editor", "Html editor")
    val html_display = menu("html_display", "Html display")
    val data_binding = menu("data_binding", "Data binding")
}