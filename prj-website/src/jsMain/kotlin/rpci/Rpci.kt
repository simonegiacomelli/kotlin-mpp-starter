@file:OptIn(InternalSerializationApi::class)

package rpci

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer
import kotlin.time.Duration

private fun second(prop: String, args: List<Any?>) {
    val elements = args.map { arg ->
        val jsonElement = when (arg) {
            null -> JsonNull
            is StringBuilder -> JsonPrimitive(arg.toString())
            else -> {
                val s = arg::class.serializer<dynamic>()
                val j = Json.encodeToJsonElement(s, arg)
                j
            }
        }
        jsonElement
    }
    val argsStr = JsonArray(elements)

    println("$prop(\n ```$argsStr```\n)")
}

fun <T> rpci(): T {
    fun js_callback_fun(target: dynamic, prop: String): dynamic {


        fun proxy(args: dynamic): dynamic {
            console.log("called =========================");
            console.log("prop", prop, "args", args)
            val argLength = args.length as Int;
            return when (prop) {
                "sum" -> {
                    val a = args[0] as Int
                    val b = args[1] as Int
                    a + b
                }

                else -> {
                    println("unhandled $prop:")
                    val argList = (0 until argLength).map { index -> args[index] as Any? }.toList()
                    second(prop, argList)
                    val argDecoded = (0 until argLength).map { index ->
                        val a = args[index] as Any?
                        "arg[$index] is " +
                                when (a) {

                                    is Instant -> {
                                        Json.encodeToString(a)
                                        "Instant is $a"
                                    }

                                    is Duration -> "Duration is $a"
                                    is StringBuilder -> "Stringbuilder is $a"
                                    null -> "null"
                                    else -> {
                                        val s = a::class.serializer<dynamic>()
                                        val j = Json.encodeToJsonElement(s, a)
                                        "unrecognized ${a::class.simpleName}, s.descriptor=${s.descriptor} j=`$j`"

                                    }
                                }
                    }.joinToString("\n") { "   $it" }
                    println(argDecoded)

                }

            }

        }

        val js_proxy = ::proxy
        return js(
            """
           function(){
            return js_proxy(arguments)
           }
        """
        )
    }

    val js_callback = ::js_callback_fun // this is needed
    return js(
        """      
        new Proxy({}, {
            get: function (target, prop) {
                return js_callback(target, prop);
            }
        });        """
    ) as T
}


fun rpciHarness() {
    println("=".repeat(30))
    println("it's rpciHarness()")
    val calculation = rpci<Calculations>()
    val result = calculation.complex1(
        Clock.System.now(),
        StringBuilder().also { it.append("<stringbuilder>") },
        SomeComplexType((Clock.System.now()))
    )
    println("result is available")
    println("result=${result}")

}

fun SerializersModuleBuilder.defaultSerializersModule() {
    polymorphic(IRpc::class) {
        subclass(SomeComplexType::class)
    }
}

val json: Json get() = Json { serializersModule = SerializersModule { defaultSerializersModule() } }
