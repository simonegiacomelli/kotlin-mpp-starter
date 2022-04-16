package rpc

import state.state

suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> Req.send(): Resp =
    sendRequest { apiName, payload -> state.dispatch(apiName, payload) }