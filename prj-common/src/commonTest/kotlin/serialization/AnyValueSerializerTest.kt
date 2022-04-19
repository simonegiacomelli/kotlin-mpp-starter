package serialization

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class AnyValueSerializerTest {
    @Test
    fun test() {
        val target = "foo".toAnyValue()
        val str = Json.encodeToString(target)
        println("serialized: `$str`")
        val restored = Json.decodeFromString<AnyValue>(str)
        println(restored)
    }
}