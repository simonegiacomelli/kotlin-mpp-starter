package databinding

import org.w3c.dom.HTMLElement

interface LookupTarget<T> : TargetProperty<T>, Observable {

}

fun <T> lookupTarget(
    target: HTMLElement,
    lookup: (String) -> T?,
    decode: (T?) -> String
): LookupTarget<T> {
    val pb = HTMLElementBridge(target)
    TODO()
}