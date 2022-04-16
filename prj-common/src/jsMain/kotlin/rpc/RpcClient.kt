package rpc

import client.StateAbs
import client.clientState
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

suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> Req.send(): Resp =
    sendRequest() { apiName, payload -> clientState.apiDispatcher(apiName, payload) }


suspend fun StateAbs.apiDispatcher(apiName: String, payload: String): String {
    val httpRequest = RpcRequest(RpcMessage(apiName, payload), session_id).toHttpRequest(ApiBaseUrl)
    val result = doFetch(httpRequest).toRpcResponse().result
    if (result.isFailure) {
        val exception = result.exceptionOrNull()!! as HttpResponseException
        println("error http status=" + exception.httpResponse.status)
        println(exception.httpResponse.body)
        throw exception
    }
    return result.getOrThrow()
}

private suspend fun StateAbs.doFetch(httpRequest: HttpRequest): HttpResponse = httpRequest.run {
    val request = RequestInit()
    request.method = "POST"
    request.body = body
    request.headers = Headers().also { headers.forEach { entry -> it.append(entry.key, entry.value) } }
    val urlWithParams = url + "?" + parameters.map { it.key + "=" + it.value }.joinToString("&")
    val resp = window.fetch(urlWithParams, request).await()
    if (!resp.ok) clientState.toast("Errore di comunicazione col server")
    val text = resp.text().await()
    // resp.headers() is present but the kotlin interface lacks the method to enumerate them so:
    return HttpResponse(text, emptyMap(), resp.status.toInt(), null)
}


