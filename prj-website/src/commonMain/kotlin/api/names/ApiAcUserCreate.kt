package api.names

import kotlinx.serialization.Serializable
import rpc.Request


@Serializable
class ApiAcUserCreateRequest(val username: String, val password: String) : Request<ApiAcUserCreateResponse>

@Serializable
class ApiAcUserCreateResponse(val message: String)

@Serializable
class ApiAcUserPasswdRequest(val username: String, val password: String) : Request<ApiAcUserPasswdResponse>

@Serializable
class ApiAcUserPasswdResponse(val message: String)
