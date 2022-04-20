package databinding

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import serialization.AnyValue

class BindableMapSerializer : KSerializer<MutableMap<String, AnyValue>> {
    override val descriptor: SerialDescriptor
        get() = TODO("Not yet implemented")

    override fun serialize(encoder: Encoder, value: MutableMap<String, AnyValue>) {
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): MutableMap<String, AnyValue> {
        TODO("Not yet implemented")
    }
}
