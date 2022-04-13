package rpc.server

import api.names.ApiDelayRequest
import api.names.ApiDelayResponse

private val reg1 = contextHandler.register { req: ApiDelayRequest, context ->
    Thread.sleep(req.delayMilliseconds)
    ApiDelayResponse()
}