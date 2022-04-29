package rpc.server

import api.names.ApiTmNewEventRequest
import database.databinding.exposedMapper
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

    val mapper = exposedMapper { TmEvent() }.bindTo(tm_events)

    val events = transaction {
        val selectAll = tm_events.slice(mapper.columns).selectAll()
        val orderBy = selectAll.orderBy(tm_events.id, SortOrder.DESC)
        orderBy.limit(10).map { mapper.map(it) }
    }
    ApiTmListEventsResponse(events)
}




