package rpc.http

data class HttpResponse(
    val body: String,
    val status: Int = 200,
    val headers: Map<String, String> = emptyMap()
)