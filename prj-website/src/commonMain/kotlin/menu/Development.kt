package menu

import accesscontrol.Role.Calculator

class Development(parent: Menu) : Menu(parent, "development", "Development") {
    val calculator = menu("calculator", "Calculator", Calculator)
}