package databinding

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import serialization.AnyValue

open class BindableSerializer<B : Bindable>(val newInstance: () -> B) : KSerializer<B> {
    private val sample = newInstance()
    private val delegateSerializer = serializer<LinkedHashMap<String, AnyValue>>()
    override val descriptor = SerialDescriptor("bindableMap", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: B) {
        encoder.encodeSerializableValue(delegateSerializer, value.bindingValueMap)
    }

    override fun deserialize(decoder: Decoder): B {
        println("deserialize()")
        val value = newInstance()
        println("deserialize() after newInstance()")
        val map = decoder.decodeSerializableValue(delegateSerializer)
        value.bindingValueMap.clear()
        value.bindingValueMap.putAll(map)
        return value
    }
}