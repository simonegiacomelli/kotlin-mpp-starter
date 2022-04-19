package databinding

interface Optional<T> {
    val value: T
    val empty: Boolean
}