package databinding

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.w3c.dom.HTMLElement


class LocalDateTimeTargetN(override val target: HTMLElement) : TargetProperty<LocalDateTime?>, HtmlElementObservable {
    private val pb = HTMLElementBridge(target)
    override var value: LocalDateTime?
        get() = runCatching { pb.value.toLocalDateTime() }.run {
            if (isFailure) console.log(exceptionOrNull())
            getOrNull()
        }
        set(value) = run { pb.value = "$value" }
}

class LocalDateTimeBridge(override val target: HTMLElement) : TargetProperty<LocalDateTime>,
    HtmlElementObservable {
    private val pb = HTMLElementBridge(target)
    override var value: LocalDateTime
        get() = pb.value.toLocalDateTime()
        set(value) = run { pb.value = "$value" }
}
