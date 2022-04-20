package databinding

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class BindableTest {

    @Serializable(with = UserSerializer::class)
    open class User : Bindable() {
        var name: String by this()
        var age: Int by this()
    }

    object UserSerializer : BindableSerializer<User>(::User)

    @Test
    fun test_serialization() {
        val original = User().apply { name = "foo"; age = 42 }
        assertEquals("foo", original.name)
        assertEquals(42, original.age)
        val str = Json.encodeToString(original)
        println("serialized instance: `$str`")
        val deserialized = Json.decodeFromString<User>(str)
        println("deserialized map: `${deserialized.bindingValueMap}`")
//        deserialized.bindingSetValue(User::name,"foo")
//        deserialized.bindingSetValue(User::age,42)
        assertEquals("foo", deserialized.name)
        assertEquals(42, deserialized.age)
    }

}
