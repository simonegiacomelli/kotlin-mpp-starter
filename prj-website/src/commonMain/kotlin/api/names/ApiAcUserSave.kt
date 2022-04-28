package api.names

import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class ApiAcUserSaveRequest(val user: AcUser) : Request<ApiAcUserSaveResponse>

@Serializable
class ApiAcUserSaveResponse(val ok: Boolean, val message: String)