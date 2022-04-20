package databinding

import kotlin.reflect.KMutableProperty0

interface Target<T> : InternallyChangeable {
    val property: KMutableProperty0<T>
}

