package menu

import accesscontrol.Role.Calculator

class Math(parent: Menu) : Menu(parent, "math", "Math") {
    val calculator = menu("calculator", "Calculator", Calculator)
}

