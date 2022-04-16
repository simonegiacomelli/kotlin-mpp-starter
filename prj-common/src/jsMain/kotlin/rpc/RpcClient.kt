package rpc

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import rpc.transport.http.*
import state.ClientState

suspend fun ClientState.apiDispatcher(apiName: String, payload: String): String {
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

private suspend fun ClientState.doFetch(httpRequest: HttpRequest): HttpResponse = httpRequest.run {
    val request = RequestInit()
    request.method = "POST"
    request.body = body
    request.headers = Headers().also { headers.forEach { entry -> it.append(entry.key, entry.value) } }
    val urlWithParams = url + "?" + parameters.map { it.key + "=" + it.value }.joinToString("&")
    val resp = window.fetch(urlWithParams, request).await()
    if (!resp.ok) toast("Errore di comunicazione col server")
    val text = resp.text().await()
    // resp.headers() is present but the kotlin interface lacks the method to enumerate them so:
    return HttpResponse(text, emptyMap(), resp.status.toInt(), null)
}


