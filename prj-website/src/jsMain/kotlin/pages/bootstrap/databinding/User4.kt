package pages.bootstrap.databinding

import kotlin.reflect.KFunction0

@kotlinx.serialization.Serializable
class User4 : Binding() {
    var age: Int by this(42)
    var degree: String by this("master")

    companion object {
        val descriptor = listOf(
            User4::age,
            User4::degree,
        )
        val constructor2: KFunction0<Binding> = ::Binding
        val constructor3 = { Binding() }
        fun x() {
            constructor2.invoke()
        }
    }

    override fun toString(): String {
        return "age=$age degree=$degree"
    }
}

