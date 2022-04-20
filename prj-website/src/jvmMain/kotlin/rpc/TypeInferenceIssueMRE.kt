// discussion:
// https://discuss.kotlinlang.org/t/generic-type-inference/24655

@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package rpc
/* @formatter:off */

import kotlinx.serialization.Serializable

fun main() {
    addHandler(AddRequest(10, 32)) { request: AddRequest ->
        AddResponse(request.a + request.b)
    }
    addHandler(AddRequest(10, 32)) { request: AddRequest ->
        //Any() // <--- compiler error, good! Required ApiResponse found Any
        error("")
    }
    addHandler2 { request: AddRequest ->
        AddResponse(request.a + request.b)
    }
    addHandler2 { request: AddRequest ->
        Any() // <--- no compiler error! one should be expected
    }
}

interface Request<Res : Any>

inline fun <reified Req : Request<Res>, reified Res : Any>
        addHandler(req: Req, noinline handler: (Req) -> Res): Res = TODO()

inline fun <reified Req : Request<Res>, reified Res : Any>
        addHandler2(noinline handler: (Req) -> Res): Res = TODO()

@Serializable class AddRequest(val a: Int, val b: Int) : Request<AddResponse>
@Serializable class AddResponse(val result: Int)