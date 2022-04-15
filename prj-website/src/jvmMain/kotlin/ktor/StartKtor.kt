package ktor

import appinit.AppInit
import appinit.destroy
import appinit.init
import folders.folders
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import rpc.RpcMessage
import rpc.oneway.OnewayContext
import rpc.oneway.onewayContextHandler
import rpc.oneway.topics.WsEndpointAnswerable
import rpc.oneway.topics.wsEndpointPool
import rpc.rpcHttpHandlerName
import rpc.server.contextHandler
import rpc.transport.http.*
import java.util.*

fun startKtor() {
    embeddedServer(CIO, port = 8080, module = Application::module).apply { start(wait = true) }
}

fun Application.module() {

    val folders = folders()
    val appInit = AppInit(folders).apply { init() }

    environment.monitor.subscribe(ApplicationStopped) { appInit.destroy() }

    val webDir = folders.data.resolve("wwwroot")
    install(WebSockets)
    install(Compression)
    install(CORS) {
        anyHost()
    }
    install(ConditionalHeaders) {
        val file = webDir.resolve("js/compiled/prj-website.js")
        version { outgoingContent ->
            println("outgoingContent=`$outgoingContent` contentType=`${outgoingContent.contentType}`")
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Application.JavaScript -> listOf(
                    EntityTagVersion(file.lastModified().hashCode().toString()),
                    LastModifiedVersion(Date(file.lastModified()))
                )
                else -> emptyList()
            }
        }
    }
//    install(CachingHeaders) {
//        options { outgoingContent ->
//            when (outgoingContent.contentType?.withoutParameters()) {
//                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 3600))
//                ContentType.Application.JavaScript -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 3600))
//                else -> null
//            }
//        }
//    }
    routing {
        static("/") {
            files(webDir)
            default(webDir.resolve("index.html"))
        }
        get("/health") {
            call.respondText { "RESULT=OK2" }
        }
        post("$rpcHttpHandlerName") {
            suspend fun HttpResponse.respondToClient() {
                headers.entries.forEach { call.response.headers.append(it.key, it.value) }
                call.respondText(
                    text = body,
                    contentType = ContentType.Text.Plain,
                    status = HttpStatusCode.fromValue(status)
                )
            }
            try {

                call.run {
                    val headersMap = request.headers.toMap().keepFirst()
                    val parametersMap = parameters.toMap().keepFirst()
                    HttpRequest(receiveText(), headersMap, "", parametersMap).toRpcRequest()
                }.run {
                    val context = session_id // todo
                    val payload = contextHandler.dispatch(message, Any())
                    RpcResponse(Result.success(payload)).toHttpResponse()
                }.respondToClient()

            } catch (ex: Exception) {
                println("Exception on api request ```${ex.stackTraceToString()}```")
                RpcResponse(Result.failure(ex)).toHttpResponse().respondToClient()
            }
        }
        webSocket("/ws1") {
            val endpoint = WsEndpointKtor(this)
            wsEndpointPool.add(endpoint)
            try {
                for (frame in incoming) {
                    println("=".repeat(10) + " /ws1")
                    when (frame) {
                        is Frame.Text -> {
                            val message = RpcMessage.decode(frame.readText())
                            onewayContextHandler.dispatchOneway(message, OnewayContext(endpoint))
                        }
                        else -> {}
                    }
                }
            } finally {
                wsEndpointPool.remove(endpoint)
            }
        }
    }
}


private fun <K, V> Map<K, List<V>>.keepFirst(): Map<K, V> =
    filterValues { it.isNotEmpty() }.mapValues { it.value.first() }


private class WsEndpointKtor(val session: DefaultWebSocketServerSession) : WsEndpointAnswerable() {
    override fun send(string: String) {
        runBlocking { session.send(string) }
    }
}