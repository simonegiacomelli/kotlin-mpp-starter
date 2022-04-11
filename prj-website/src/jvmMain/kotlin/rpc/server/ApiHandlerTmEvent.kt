package rpc.server

import api.names.ApiTmEventRequest
import api.names.ApiTmEventResponse

private val reg1 = contextHandler.register { req: ApiTmEventRequest, context ->
    ApiTmEventResponse(req.a + req.b)
}