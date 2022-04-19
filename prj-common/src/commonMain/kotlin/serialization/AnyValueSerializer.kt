package serialization

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.serializer
import rpc.nameOf
import kotlin.reflect.KClass

object AnyValueSerializer : KSerializer<AnyValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Packet") {
        element("dataType", serialDescriptor<String>())
        element("payload", buildClassSerialDescriptor("payload"))
    }

    override fun serialize(encoder: Encoder, value: AnyValue) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, nameOf(value.kClass))
            encodeSerializableElement(descriptor, 1, value.kClass.serializerExtension, value.payload)
        }
    }

    override fun deserialize(decoder: Decoder): AnyValue = decoder.decodeStructure(descriptor) {
        if (decodeSequentially()) {
            val kClassName = decodeStringElement(descriptor, 0)
            val kClass = nameToKClass(kClassName)
            val payload = decodeSerializableElement(descriptor, 1, kClass.serializerExtension)
            AnyValue(kClass, payload)
        } else {
            require(decodeElementIndex(descriptor) == 0) { "dataType field should precede payload field" }
            val kClassName = decodeStringElement(descriptor, 0)
            val kClass = nameToKClass(kClassName)
            val payload = when (val index = decodeElementIndex(descriptor)) {
                1 -> decodeSerializableElement(descriptor, 1, kClass.serializerExtension)
                CompositeDecoder.DECODE_DONE -> throw SerializationException("payload field is missing")
                else -> error("Unexpected index: $index")
            }
            AnyValue(kClass, payload)
        }
    }
}

val KClass<*>.serializerExtension: KSerializer<Any?>
    get() = serializers[this]
        ?: throw SerializationException("No registered serializer for class `${this.simpleName}`. Search for ${::serializers.name} and add it to the list.")

private val serializers: Map<KClass<*>, KSerializer<Any?>> = mapOf(
    pair<Int>(),
    pair<String>(),
    pair<Boolean>(),
    pair<Long>(),
    pair<Double>(),
    pair<LocalDateTime>(),
    pair<LocalDate>(),
)
private val nameToClass = serializers.map { nameOf(it.key) to it.key }.toMap()

@Suppress("UNCHECKED_CAST")
inline fun <reified T> pair(): Pair<KClass<*>, KSerializer<Any?>> =
    T::class to serializer<T?>() as KSerializer<Any?>

fun nameToKClass(name: String): KClass<*> = nameToClass[name] ?: error("Class not found $name")

