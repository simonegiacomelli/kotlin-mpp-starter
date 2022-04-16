package rpc

import client.state

suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> Req.send(): Resp =
    sendRequest { apiName, payload -> state.dispatch(apiName, payload) }