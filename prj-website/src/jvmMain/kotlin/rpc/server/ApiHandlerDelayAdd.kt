package rpc.server

import api.names.ApiDelayRequest
import rpc.VoidResponse

private val reg1 = contextHandler.register { req: ApiDelayRequest, context ->
    Thread.sleep(req.delayMilliseconds)
    VoidResponse()
}