package api.names

import context.UserDc
import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class Credential(val username: String, val password: String)

@Serializable
class ApiAcLoginRequest(val credential: Credential) : Request<ApiAcSessionResponse>

@Serializable
class ApiAcSession(val id: String, val user: UserDc)

@Serializable
class ApiAcSessionResponse(val session: ApiAcSession?)


@Serializable
class ApiAcVerifySessionRequest(val id: String) : Request<ApiAcSessionResponse>