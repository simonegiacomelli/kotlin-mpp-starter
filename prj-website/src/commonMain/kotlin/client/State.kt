package client

var stateOrNull: () -> State = { error("no State handler") }
val state get() = stateOrNull()

