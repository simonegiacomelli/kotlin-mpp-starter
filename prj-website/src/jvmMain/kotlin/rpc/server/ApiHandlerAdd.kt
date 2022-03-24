package rpc.server

import api.names.ApiAddRequest
import api.names.ApiAddResponse

private val reg1 = contextHandler.register { req: ApiAddRequest, context ->
    ApiAddResponse(req.a + req.b)
}