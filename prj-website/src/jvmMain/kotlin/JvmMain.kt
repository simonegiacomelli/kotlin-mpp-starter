import appinit.AppInit
import appinit.destroy
import appinit.init
import appinit.waitJdbcInfo
import folders.Folders
import folders.data.etc.config
import folders.data.etc.database
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
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import rpc.RpcMessage
import rpc.oneway.OnewayContext
import rpc.oneway.onewayContextHandler
import rpc.oneway.topics.WsEndpointAnswerable
import rpc.oneway.topics.wsEndpointPool
import rpc.rpcHttpHandlerName
import rpc.server.contextHandler
import java.io.File
import java.util.*


fun main() {
    println("=".repeat(50))
    println("Working folder ${File(".").canonicalPath}")
    embeddedServer(CIO, port = 8080, module = Application::module).apply { start(wait = true) }
}

fun Application.module() {

    val folders = Folders(File("."))
    val config = folders.config()
    val jdbc = config.waitJdbcInfo()
    val database = config.database()
    val contextInit = AppInit(folders, config, jdbc, database)

    contextInit.init()
    environment.monitor.subscribe(ApplicationStopped) { contextInit.destroy() }

    val webDir = folders.data.resolve("wwwroot")
    install(WebSockets)
    install(Compression)
    install(CORS) {
        anyHost()
    }
    install(ConditionalHeaders) {
        val file = webDir.resolve("js/compiled/prj-website.js")
        version { outgoingContent ->
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
            try {
                val apiName = call.parameters["api_name"]!!
                println("dispatching $apiName")
                val serializedResponse =
                    contextHandler.dispatch(apiName, call.receiveText(), Any())
                call.respondText("success=1\n\n$serializedResponse", ContentType.Text.Plain)
            } catch (ex: Exception) {
                val text = "success=0\n\n${ex.stackTraceToString()}"
                println("handling exception [[$text]] ")
                call.respondText(
                    text = text,
                    status = HttpStatusCode.InternalServerError,
                    contentType = ContentType.Text.Plain
                )
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

private class WsEndpointKtor(val session: DefaultWebSocketServerSession) : WsEndpointAnswerable() {
    override fun send(string: String) {
        runBlocking { session.send(string) }
    }
}