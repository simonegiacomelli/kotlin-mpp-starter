package databinding

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class BindableTest {
    @Serializable
    class GuineaPig(val user: User)

    @Serializable(with = UserSerializer::class)
    open class User : Bindable() {
        var name: String by this()
        var age: Int by this()
    }

    object UserSerializer : BindableSerializer<User>(::User)

    @Test
    fun test_serialization() {
        val original = GuineaPig(User().apply { name = "foo"; age = 42 })
        fun verify(u: User) = u.apply {
            assertEquals("foo", name)
            assertEquals(42, age)
        }
        verify(original.user)

        val str = Json.encodeToString(original)
        println("serialized instance: `$str`")
        val deserialized = Json.decodeFromString<GuineaPig>(str)
        println("deserialized map: `${deserialized.user.bindingValueMap}`")
        verify(deserialized.user)
    }

}
