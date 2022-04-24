package rpc.server

import databinding.funMapper
import telemetry.api.TmEvent
import kotlin.test.Test


class ApiHandlerTmEventKtTest {

    @Test
    fun test_dynamicKFunctionInvoke() {
        val mapper4 = funMapper(::TmEvent)
    }

}