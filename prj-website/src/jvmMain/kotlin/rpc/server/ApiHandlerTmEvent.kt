package rpc.server

import api.names.ApiTmNewEventRequest
import database.schema.tm_events
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import rpc.VoidResponse
import telemetry.api.ApiTmListEventsRequest
import telemetry.api.ApiTmListEventsResponse
import telemetry.api.TmEvent
import telemetry.newEvent

private val reg1 = contextHandler.register { req: ApiTmNewEventRequest, _ ->
    transaction { newEvent(db, req.type_id, req.arguments) }
    VoidResponse
}

private val reg2 = contextHandler.register { req: ApiTmListEventsRequest, _ ->
    val events = transaction {
        tm_events.selectAll().orderBy(tm_events.id, SortOrder.DESC).limit(50).map {
            TmEvent(
                it[tm_events.id].value,
                it[tm_events.type_id],
                it[tm_events.arguments],
                it[tm_events.created_at],
            )
        }
    }
    ApiTmListEventsResponse(events)
}

