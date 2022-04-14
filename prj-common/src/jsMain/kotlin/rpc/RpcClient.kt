package rpc

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import rpc.transport.http.*

object Api {
    @Deprecated(
        message = "use the extension function on all Request<>",
        replaceWith = ReplaceWith("request.send()")
    )
    suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> send(
        request: Req,
    ): Resp {
        return request.send()
    }
}

suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> Req.send(): Resp = send(this, ::dispatcher)

var ApiBaseUrl = ""
suspend fun dispatcher(apiName: String, payload: String): String {
    val httpRequest = RpcRequest(RpcMessage(apiName, payload), session_id).toHttpRequest(ApiBaseUrl)
    val result = httpRequest.fetch().toRpcResponse().result
    if (result.isFailure)
        throw result.exceptionOrNull()!!
    return result.getOrThrow()
}

private suspend fun HttpRequest.fetch(): HttpResponse {
    val request = RequestInit()
    request.method = "POST"
    request.body = body
    request.headers = Headers().also { headers.forEach { entry -> it.append(entry.key, entry.value) } }
    val urlWithParams = url + "?" + parameters.map { it.key + "=" + it.value }.joinToString("&")
    val resp = window.fetch(urlWithParams, request).await()
    val text = resp.text().await()
    // resp.headers() is present but the kotlin interface lacks the method to enumerate them so:
    return HttpResponse(text, emptyMap(), resp.status.toInt(), null)
}


