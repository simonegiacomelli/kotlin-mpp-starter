package client

import rpc.Request
import rpc.sendRequest

suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> State.send(request: Req):
        Resp = request.sendRequest(::dispatch)