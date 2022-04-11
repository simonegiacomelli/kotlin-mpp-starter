package appinit

import html.SignalHtmlThread
import rpc.oneway.OnewayContextHandler
import rpc.oneway.onewayContextHandler
import rpc.oneway.topics.*
import rpc.server.ContextHandler
import utils.AutoLoadPackage

internal fun AppInit.initKotlinPart() {
    destroyCallback.add { wsEndpointPool.destroy() }
    loadHandlersClasses()
    wsEndpointPoolBacking = WsEndpointPool(folders.data.proc)
    setupTopicInfrastructure(onewayContextHandler, folders)
    if (config.watch_design_html) startDesignWatcher()
}

private fun AppInit.startDesignWatcher() {
    val sign = SignalHtmlThread(folders.data.html.resolve("design.html"))
    destroyCallback.add { sign.destroy() }
}

private fun loadHandlersClasses() {
    AutoLoadPackage().loadClasses(ContextHandler::class.java.`package`.name)
    AutoLoadPackage().loadClasses(OnewayContextHandler::class.java.`package`.name)
    AutoLoadPackage().loadClasses(WsEndpoint::class.java.`package`.name)
}