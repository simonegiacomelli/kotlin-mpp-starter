package databinding

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class BindableTest {

//    @Serializable
//    open class User  {
//        val bindable = Bindable()
//        var name: String by bindable()
//        var age: Int by bindable()
//    }


    @Test
    fun test_serialization() {
        val original = User().apply { name = "foo"; age = 42 }
        val str = Json.encodeToString(original)
        println("serialized instance: `$str`")
        val deserialized = Json.decodeFromString<User>(str)
        assertEquals("foo", deserialized.name)
        assertEquals(42, deserialized.age)
    }

}
