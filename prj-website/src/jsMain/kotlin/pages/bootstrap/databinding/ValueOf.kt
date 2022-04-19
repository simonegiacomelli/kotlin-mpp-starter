package pages.bootstrap.databinding

fun <T> valueOf(value: T): Optional<T> = object : Optional<T> {
    override val value: T get() = value
    override val empty: Boolean get() = true
}