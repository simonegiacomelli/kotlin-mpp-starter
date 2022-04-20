package pages.bootstrap.databinding.demo

import org.w3c.dom.HTMLInputElement
import pages.bootstrap.commonwidgets.InputGroupWidget
import widget.Widget

class DoubleInputResultWidget : Widget(//language=HTML
    """
<span id='inputA'></span>
<span id='inputB'></span>

<div class="form-floating mb-3">
    <input id='inputResult' type="email" class="form-control" id="floatingInput" placeholder="">
    <label for="floatingInput">A + B =</label>
</div>
"""
) {
    val inputA by this { InputGroupWidget() }
    val inputB by this { InputGroupWidget() }
    val inputResult: HTMLInputElement by this
}