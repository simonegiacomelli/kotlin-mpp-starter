package grid


import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

data class Property<E, out V>(
    val name: String,
    var caption: String = name,
    val kClass: KClass<*>,
    var onDataRender: (ValueEvent<E>.() -> Unit)? = null,
    var onHeadRender: (PropertyEvent<E>.() -> Unit)? = null,
    val get: (receiver: E) -> V,
) {
    inline fun <reified Q> asWrapped(
        name: String = this.name,
        caption: String = this.caption,
        noinline unwrap: Q.() -> E
    ) = Property<Q, V>(name, caption, kClass) { receiver -> get(receiver.unwrap()) }

}

inline fun <E, reified V> property(
    name: String,
    caption: String = name,
    kClass: KClass<*> = V::class,
    noinline onDataRender: (ValueEvent<E>.() -> Unit)? = null,
    noinline onHeadRender: (PropertyEvent<E>.() -> Unit)? = null,
    noinline get: (receiver: E) -> V
): Property<E, V> = Property(name, caption, kClass, onDataRender, onHeadRender, get)

inline fun <reified E, reified V> KProperty1<E, V>.asProperty(
    caption: String = this.name,
    noinline onDataRender: (ValueEvent<E>.() -> Unit)? = null,
    noinline onHeadRender: (PropertyEvent<E>.() -> Unit)? = null,
    name: String = this.name,
) = Property(name, caption, V::class, onDataRender = onDataRender, onHeadRender = onHeadRender, get = ::get)

/**  Avrei usato KProperty1 direttamente al posto di Property1, ma purtroppo non e' istanziabile in kotlin/js */

inline fun <reified E, reified V, reified Q> KProperty1<E, V>.asProperty(
    caption: String = this.name,
    name: String = this.name,
    noinline onDataRender: (ValueEvent<E>.() -> Unit)? = null,
    noinline onHeadRender: (PropertyEvent<E>.() -> Unit)? = null,
    noinline map: E.(V) -> Q
) = Property<E, Q>(
    name,
    caption,
    Q::class,
    onDataRender = onDataRender,
    onHeadRender = onHeadRender
) { receiver -> receiver.map(get(receiver)) }


fun <T, R> Property<T, R>.asProperty(
    name: String = this.name,
    caption: String = name,
) = Property(name, caption, kClass, get = get)

inline fun <T, reified R, reified Q> Property<T, R>.asProperty(
    name: String = this.name,
    caption: String = name,
    noinline map: T.(R) -> Q
) = Property<T, Q>(name, caption, R::class) { receiver -> receiver.map(get(receiver)) }

