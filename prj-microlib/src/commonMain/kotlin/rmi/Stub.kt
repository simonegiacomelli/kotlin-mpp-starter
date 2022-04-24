package rmi

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface Request<Res>


interface Transport {
    /** if in stub, this is clientHandle=fetch
     * if in skeleton this is serverHandle=ktor */
    suspend fun handle(packet: Packet): Packet
}

abstract class Translator {
    open fun augment(packet: Packet): Packet = packet
    open fun decrease(packet: Packet) = packet

    inline fun <reified R> pack(res: R): Packet = TODO()
    inline fun <reified R> unpack(packet: Packet): R = TODO()
}

abstract class Stub {
    open fun augment(packet: Packet): Packet = packet
    open fun decrease(packet: Packet) = packet

    inline fun <reified R> pack(res: R): Packet = Packet(Json.encodeToString(res))
    inline fun <reified R> unpack(packet: Packet): R = Json.decodeFromString(packet.body)

    abstract val transport: Transport
}


data class Packet(val body: String, val headers: MutableMap<String, String> = mutableMapOf())

suspend inline fun <reified Res> Request<Res>.send(stub: Stub): Res = TODO()

