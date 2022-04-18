package pages.bootstrap.databinding

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import org.w3c.dom.HTMLInputElement
import kotlin.reflect.KMutableProperty0

fun localDateBridge(target: HTMLInputElement): KMutableProperty0<LocalDate?> = LocalDateBridge(target)::value
fun localDateTarget(target: HTMLInputElement) = HtmlInputTarget(target, localDateBridge(target))
private class LocalDateBridge(val target: HTMLInputElement) {
    var value: LocalDate?
        get() = runCatching { target.value.toLocalDate() }.run {
            if (isFailure) console.log(exceptionOrNull())
            getOrNull()
        }
        set(value) = run { target.value = "$value" }
}
