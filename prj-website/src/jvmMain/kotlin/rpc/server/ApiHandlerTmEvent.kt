package rpc.server

import api.names.ApiTmEventRequest
import api.names.ApiTmEventResponse
import org.jetbrains.exposed.sql.transactions.transaction
import telemetry.newEvent

private val reg1 = contextHandler.register { req: ApiTmEventRequest, _ ->
    transaction { newEvent(db, req.type_id, req.arguments) }
    ApiTmEventResponse()
}