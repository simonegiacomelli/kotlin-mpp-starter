package pages.bootstrap.commonwidgets

import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import widget.Widget

class InputGroupWidget : Widget(//language=HTML
    """
<div class="input-group mb-3">
    <span id='addon' class="input-group-text" id="basic-addon1">A</span>
    <input id='input' type="text" class="form-control" placeholder="" aria-label=""
           aria-describedby="basic-addon1">
</div>
"""
) {
    val input: HTMLInputElement by this
    val addon: HTMLElement by this
}