package rmi

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test

class StubTest {

    @Serializable
    class AddRequest(val a: Int, val b: Int) : Request<AddResponse>

    @Serializable
    class AddResponse(val result: Int)

    @Test
    fun test_send() = runTest {
        val wire = mutableListOf<Packet>()


//        AddRequest(7, 35).send(stub)


    }
}