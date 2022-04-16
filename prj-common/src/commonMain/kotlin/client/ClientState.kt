package client

interface ClientState {
    fun toast(message: String)
    val ApiBaseUrl: String
    val session_id: String?
}


var clientStateOrNull: () -> ClientState = { error("non ClientState handler") }
val clientState get() = clientStateOrNull()