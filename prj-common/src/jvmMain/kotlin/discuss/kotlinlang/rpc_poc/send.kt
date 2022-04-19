@file:Suppress("UNCHECKED_CAST", "UNUSED_ANONYMOUS_PARAMETER")
@file:OptIn(ExperimentalSerializationApi::class)
/* @formatter:off */

package discuss.kotlinlang.rpc_poc

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

inline fun <reified Req : Request<Res>, reified Res : Any> Req.send(): Res = TODO()
inline fun <reified Req : Request<Res>, reified Res : Any> addHandler(noinline handler: (Req) -> Res): Unit = TODO()

interface Request<in Res : Any>
@Serializable data class SumRequest(val a: Int, val b: Int) : Request<SumResponse>
@Serializable data class SumResponse(val sum: Int)

object IssueRequest {

    @JvmStatic
    fun main(args: Array<String>) {
        // this is ok: the type of send() is SumResponse, as expected
        println(SumRequest(30, 12).send().sum)
    }
}

object HandleRequest {

    @JvmStatic
    fun main(args: Array<String>) {

        // as I usually use it
        addHandler { request: SumRequest -> SumResponse(request.a + request.b) }

        // ok, no news here
        addHandler<SumRequest, SumResponse> { request: SumRequest -> SumResponse(request.a + request.b) }

        // this is the issue: I would expect a compiler error because Any() is not
        // coherent with declaration above SumRequest(...) : Request<SumResponse>
        addHandler { request: SumRequest -> Any() }

        // this does not compile with error: Type mismatch: inferred type is Any but SumResponse was expected
        // so, why is it that the previous invocation is fine with the compiler?!
        /*
        addHandler<SumRequest, SumResponse> { request: SumRequest -> Any() }
        */
    }
}

