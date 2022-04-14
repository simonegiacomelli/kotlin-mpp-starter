package rpc.server

import api.names.ApiAddRequest
import api.names.ApiAddResponse

private val reg1 = contextHandler.register { req: ApiAddRequest, context ->
    val result = req.a + req.b
    if (result > 42) throw InterruptedException("such an error!")
    ApiAddResponse(result)
}