package rpci

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

interface IRpc

@Serializable
data class SomeComplexType(val instant: Instant) : IRpc


@JsExport
interface Calculations {
    fun sum(a: Int, b: Int): Any?
    fun complex1(a: Instant, b: StringBuilder, sct: SomeComplexType): Any?
}