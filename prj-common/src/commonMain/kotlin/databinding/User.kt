package databinding

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
open class User : Bindable() {
    var name: String by this()
    var age: Int by this()
}


class UserSerializer : KSerializer<User> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Packet") {
        element("dataType", serialDescriptor<String>())
        element("payload", buildClassSerialDescriptor("payload"))
    }

    override fun serialize(encoder: Encoder, value: User) {
        println("serializer------------")
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): User {
        println("deserialize------------")
        TODO("Not yet implemented")
    }
}