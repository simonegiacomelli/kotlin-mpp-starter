package serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.test.fail

class SerializationTest {
    @Serializable
    class GuineaPig1(val string1: String, val int1: Int, val list1: List<String>)

    //    @Test
    fun t1() {
        val d = GuineaPig1.serializer().descriptor
        log(d, 0)
        fail(":)")
    }

    private fun log(d: SerialDescriptor, indent: Int) {
        println(" ".repeat(indent) + d.serialName + " " + d.kind)
        repeat(d.elementsCount) {
            log(d.getElementDescriptor(it), it + 1)
        }
    }
}