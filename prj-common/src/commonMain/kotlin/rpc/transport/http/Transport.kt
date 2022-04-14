package rpc.transport.http

// interface to fetch/XMLHttpRequest
// interface to api server/servlet-container/etc
fun interface Transport {
    fun process(request: Request): Response
}

class Request(
    val body: String,
    val headers: Map<String, String> = emptyMap(),
    val url: String,
)

class Response(
    val body: String,
    val headers: Map<String, String> = emptyMap(),
    val status: Int,
    val exception: String?
)
