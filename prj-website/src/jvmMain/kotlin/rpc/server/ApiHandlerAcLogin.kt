package rpc.server

import api.names.ApiAcLoginRequest
import api.names.ApiAcLoginResponse

private val reg1 = contextHandler.register { req: ApiAcLoginRequest, _ ->
    ApiAcLoginResponse()
}