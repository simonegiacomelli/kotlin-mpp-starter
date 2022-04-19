package databinding

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
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

class BindableSerializer : KSerializer<Bindable> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Packet") {
        element("dataType", serialDescriptor<String>())
        element("payload", buildClassSerialDescriptor("payload"))
    }

    override fun serialize(encoder: Encoder, value: Bindable) {
        println("serializer------------")
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): Bindable {
        println("deserialize------------")
        TODO("Not yet implemented")
    }
}