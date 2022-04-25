package databinding

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.w3c.dom.HTMLElement


class LocalDateTimeBridge(override val target: HTMLElement) : PropertyBridge<LocalDateTime?>, HtmlElementObservable {
    private val pb = HTMLElementBridge(target)
    override var value: LocalDateTime?
        get() = runCatching { pb.value.toLocalDateTime() }.run {
            if (isFailure) console.log(exceptionOrNull())
            getOrNull()
        }
        set(value) = run { pb.value = "$value" }
}

class LocalDateTimeBridgeNN(override val target: HTMLElement) : PropertyBridge<LocalDateTime>,
    HtmlElementObservable {
    private val pb = HTMLElementBridge(target)
    override var value: LocalDateTime
        get() = pb.value.toLocalDateTime()
        set(value) = run { pb.value = "$value" }
}
