package menu

import accesscontrol.Role

class Telemetry(parent: Menu) : Menu(parent, "telemetry", "Telemetry", Role.Admin) {
    val tm_events = menu("tm_events", "Events")
}