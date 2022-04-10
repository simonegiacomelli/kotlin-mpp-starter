package utils

class ClearableLazy<T>(private val closeResources: (T) -> Unit = {}, val initializer: () -> T) : Lazy<T> {
    private var inst: T? = null
    override val value: T get() = inst ?: initializer().also { inst = it }
    override fun isInitialized() = inst != null
    fun clear() {
        if (isInitialized()) {
            closeResources(value)
            inst = null
        }
    }
}