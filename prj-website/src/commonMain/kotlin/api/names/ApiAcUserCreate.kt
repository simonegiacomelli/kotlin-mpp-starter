package api.names

import kotlinx.serialization.Serializable
import rpc.Request


@Serializable
class ApiAcUserCreateRequest(val username: String, val password: String) : Request<ApiAcUserCreateResponse>

@Serializable
class ApiAcUserCreateResponse()
