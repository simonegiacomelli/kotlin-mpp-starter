package databinding

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import org.w3c.dom.HTMLInputElement


class LocalDateBridge(override val target: HTMLInputElement) : PropertyBridge<LocalDate?>, HtmlInputTarget {
    override var value: LocalDate?
        get() = runCatching { target.value.toLocalDate() }.run {
            if (isFailure) console.log(exceptionOrNull())
            getOrNull()
        }
        set(value) = run { target.value = "$value" }
}
