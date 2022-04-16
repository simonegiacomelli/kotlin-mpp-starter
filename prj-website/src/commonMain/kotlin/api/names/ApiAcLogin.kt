package api.names

import accesscontrol.Session
import kotlinx.serialization.Serializable
import rpc.Request
import rpc.VoidResponse

@Serializable
class Credential(val username: String, val password: String)

@Serializable
class ApiAcLoginRequest(val credential: Credential) : Request<ApiAcSessionResponse>

@Serializable
class ApiAcSessionResponse(val session: Session?)

@Serializable
class ApiAcVerifySessionRequest(val id: String) : Request<ApiAcSessionResponse>

@Serializable
class ApiAcPasswordChangeRequest(val password: String) : Request<VoidResponse>
