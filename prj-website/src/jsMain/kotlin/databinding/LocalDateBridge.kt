package databinding

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import org.w3c.dom.HTMLInputElement
import pages.bootstrap.databinding.HtmlInputTarget
import kotlin.reflect.KMutableProperty0

fun localDateBridge(target: HTMLInputElement): KMutableProperty0<LocalDate?> = LocalDateBridge(target)::value
fun localDateTarget(target: HTMLInputElement) = HtmlInputTarget(target, localDateBridge(target))

class LocalDateBridge(override val target: HTMLInputElement) : PropertyBridge<LocalDate?>,
    HtmlInputTarget2<LocalDate?> {
    override var value: LocalDate?
        get() = runCatching { target.value.toLocalDate() }.run {
            if (isFailure) console.log(exceptionOrNull())
            getOrNull()
        }
        set(value) = run { target.value = "$value" }

    override val property: KMutableProperty0<LocalDate?>
        get() = this::value
}
