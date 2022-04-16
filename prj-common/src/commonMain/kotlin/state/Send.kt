package state

import rpc.Request
import rpc.sendRequest

suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> ClientState.send(request: Req):
        Resp = request.sendRequest(::dispatch)