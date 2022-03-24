package utils

import java.io.Closeable

class ClearableLazy<T>(private val closeResources: (T) -> Unit = {}, val initializer: () -> T) : Lazy<T>, Closeable {
    private var inst: T? = null
    override val value: T get() = inst ?: initializer().also { inst = it }
    override fun isInitialized() = inst != null
    override fun close() {
        if (isInitialized()) {
            closeResources(value)
            inst = null
        }
    }
}