package databinding

fun <T> empty(): Optional<T> = object : Optional<T> {
    override val value: T get() = error("no value")
    override val empty: Boolean get() = false
}