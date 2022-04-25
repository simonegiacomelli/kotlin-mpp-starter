package telemetry.api

import databinding.Bindable
import databinding.BindableSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import rpc.Request


@Serializable
class ApiTmListEventsRequest() : Request<ApiTmListEventsResponse>


@Serializable(with = TmEventSerializer::class)
class TmEvent : Bindable() {
    var id: Long by this(0)
    var type_id: Int by this(0)
    var arguments: String by this("")
    var created_at: LocalDateTime by this(now())
}

object TmEventSerializer : BindableSerializer<TmEvent>(::TmEvent)

private fun now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())


@Serializable
class ApiTmListEventsResponse(val events: List<TmEvent>)


