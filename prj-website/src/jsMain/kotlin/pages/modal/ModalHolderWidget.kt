package pages.modal

import kotlinx.dom.clear
import org.w3c.dom.HTMLDivElement
import widget.HolderWidget
import widget.Widget

open class ModalHolderWidget : HolderWidget(//language=HTML
    """
    $css
    <div id="modalContainer" class='k-modal' style='display: none'>
        <div>
            <div id="modalContent" class='containerHolderModal'></div>
        </div>
    </div>
    """.trimIndent()
) {
    private val modalContainer: HTMLDivElement by this
    private val modalContent: HTMLDivElement by this
    override val containerProvider get() = modalContent

    override fun show(widget: Widget) {
        if (stack.isEmpty()) showModalWidget()
        super.show(widget)
    }

    override fun closeCurrent() {
        if (stack.size == 1) {
            hideModalWidget()
            containerProvider.clear()
            stack.clear()
        } else
            super.closeCurrent()
    }

    private fun showModalWidget() {
        modalContainer.style.display = "flex"
        modalContainer.onmousedown = { closeCurrent() }
        modalContent.onmousedown = { it.stopPropagation() }
    }

    private fun hideModalWidget() = run { modalContainer.style.display = "none" }

}


private val css = //language=HTML
    """
<style>    
    .k-modal {
        display: flex;
        align-content: center;
        justify-content: center;
        align-items: center;
        position: fixed;
        z-index: 997;
        /*padding-top: 100px;*/
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        overflow: auto;
        background-color: rgb(0, 0, 0);
        background-color: rgba(0, 0, 0, 0.8);
    }
    
    .containerHolderModal {
        height: 100vh;
        width: 100vw;
        display: block;
        background: #F5F5F5;
        border: 1px solid #707070;
        border-radius: 3em;
        padding: 1em;
        min-width: 33em;
        max-width: 60em;
        min-height: 33em;
        max-height: 45em;
    }

</style>
""".trimIndent()