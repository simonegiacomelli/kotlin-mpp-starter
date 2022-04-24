package telemetry.api

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import rpc.Request


@Serializable
class ApiTmListEventsRequest() : Request<ApiTmListEventsResponse>


@Serializable
class TmEvent(val id: Long, val type_id: Int, val arguments: String, val created_at: LocalDateTime)

@Serializable
class ApiTmListEventsResponse(val events: List<TmEvent>)


