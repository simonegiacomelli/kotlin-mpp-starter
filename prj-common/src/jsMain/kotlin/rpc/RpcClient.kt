package rpc

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import rpc.http.FetchRequest
import rpc.http.FetchResponse

object Api {
    suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> send(
        request: Req,
    ): Resp {
        return send(request, ::dispatcher)
    }
}

suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> Req.send(): Resp = Api.send(this)

var ApiBaseUrl = ""
suspend fun dispatcher(apiName: String, payload: String): String =
    FetchRequest(ApiBaseUrl, apiName, payload, session_id).values().run {
        val request = RequestInit()
        request.method = method
        request.body = body
        request.headers = Headers().also { headers.forEach { entry -> it.append(entry.key, entry.value) } }
        val resp = window.fetch(url, request).await()
        FetchResponse(resp.status, resp.text().await()).payload()
    }
