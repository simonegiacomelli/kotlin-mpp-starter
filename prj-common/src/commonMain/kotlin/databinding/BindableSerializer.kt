package databinding

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import serialization.AnyValue

/** this could be made much more efficient:
 * At runtime we already know the klass for each field (thanks to the reified delegateProvider),
 * so there is no actual need to specify the type everytime for each!
 * Using the AnyValue we are actually doing it */
open class BindableSerializer<B : Bindable>(val newInstance: () -> B) : KSerializer<B> {
    private val sample = newInstance()
    private val delegateSerializer = serializer<LinkedHashMap<String, AnyValue>>()
    override val descriptor = SerialDescriptor("bindableMap", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: B) {
        encoder.encodeSerializableValue(delegateSerializer, value.bindingValueMap)
    }

    override fun deserialize(decoder: Decoder): B {
        val value = newInstance()
        val srcMap = decoder.decodeSerializableValue(delegateSerializer)

        value.bindingValueMap.forEach { dst ->
            val srcEntry = srcMap[dst.key]
            if (srcEntry != null) dst.value.payload = srcEntry.payload
        }

        return value
    }
}