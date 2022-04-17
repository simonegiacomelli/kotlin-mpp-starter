package ktor

import accesscontrol.Role
import appinit.destroy
import appinit.initWebPart
import appinit.newState
import context.RequestDispatcher
import context.authorizeDispatch
import context.contextFactory
import heap.HeapDumper
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
import menu.toRoleMap
import rpc.RpcMessage
import rpc.oneway.OnewayContext
import rpc.oneway.onewayContextHandler
import rpc.oneway.topics.WsEndpointAnswerable
import rpc.oneway.topics.wsEndpointPool
import rpc.rpcHttpHandlerName
import rpc.server.contextHandler
import rpc.transport.http.HttpRequest
import rpc.transport.http.HttpResponse
import java.util.*

fun startKtor() {
    embeddedServer(CIO, port = 8080, module = Application::module).apply { start(wait = true) }
}

fun Application.module() {

    val state = newState().apply { initWebPart() }
    val folders = state.folders
    HeapDumper.enableHeapDump(folders.data.heapdump)

    environment.monitor.subscribe(ApplicationStopped) { state.destroy() }

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
        val roles = Role.values().toSet().toRoleMap()
        post("$rpcHttpHandlerName") {
            suspend fun HttpResponse.respondToClient() {
                headers.entries.forEach { call.response.headers.append(it.key, it.value) }
                call.respondText(
                    text = body,
                    contentType = ContentType.Text.Plain,
                    status = HttpStatusCode.fromValue(status)
                )
            }
            RequestDispatcher(
                dispatcher = { message, ctx -> contextHandler.dispatch(message, ctx) },
                contextFactory = { state.contextFactory(it) },
                authorized = { message, context -> authorizeDispatch(message, context.user, roles) },
                httpRequest = {
                    val headersMap = call.request.headers.toMap().fix()
                    val parametersMap = call.parameters.toMap().fix()
                    HttpRequest(call.receiveText(), headersMap, "", parametersMap)
                }).dispatch().respondToClient()
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


private fun Map<String, List<String>>.fix(): Map<String, String> =
    filterValues { it.isNotEmpty() }.mapValues { it.value.first() }.mapKeys { it.key.lowercase() }


private class WsEndpointKtor(val session: DefaultWebSocketServerSession) : WsEndpointAnswerable() {
    override fun send(string: String) {
        runBlocking { session.send(string) }
    }
}