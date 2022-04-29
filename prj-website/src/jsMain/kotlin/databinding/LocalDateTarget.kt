package databinding

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import org.w3c.dom.HTMLElement


class LocalDateTarget(override val target: HTMLElement) : TargetProperty<LocalDate?>, HtmlElementObservable {
    private val pb = HTMLElementBridge(target)
    override var value: LocalDate?
        get() = runCatching { pb.value.toLocalDate() }.run {
            if (isFailure) console.log(exceptionOrNull())
            getOrNull()
        }
        set(value) = run { pb.value = "$value" }
}
