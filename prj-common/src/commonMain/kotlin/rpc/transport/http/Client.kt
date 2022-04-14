package rpc.transport.http

// interface to fetch/XMLHttpRequest

fun interface Transport {
    fun fetch(request: WireRequest): WireResponse
}

class WireRequest(
    val url: String,
    val headers: Map<String, String> = emptyMap(),
    val payload: String,
)

class WireResponse(
    val status: Int,
    val body: String,
    val headers: Map<String, String> = emptyMap(),
    val exception: String?
)


// interface to api server/servlet-container/etc
fun interface Server {
    fun handle(request: WireRequest): WireResponse
}