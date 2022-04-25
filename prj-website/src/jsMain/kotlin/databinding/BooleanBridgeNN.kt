package databinding

import org.w3c.dom.HTMLInputElement
import utils.forward

class BooleanBridgeNN(override val target: HTMLInputElement) : PropertyBridge<Boolean>, HtmlElementObservable {
    override var value by forward { target::checked }
}


