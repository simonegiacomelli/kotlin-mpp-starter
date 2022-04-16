package state

var stateOrNull: () -> ClientState = { error("no State handler") }
val state get() = stateOrNull()

