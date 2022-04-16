package pages

import extensions.extVisible
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import utils.forward
import utils.launchJs
import widget.Widget

class LoaderWidget : Widget(//language=HTML
    """
<style>
    #div_loader2 {
        border: 4px solid #f3f3f3; /* Light grey */
        border-top: 4px solid #3498db; /* Blue */
        border-radius: 50%;
        width: 30px;
        height: 30px;
        animation: spin 0.5s linear infinite;
        position: fixed;
        left: 0px;
        top: 0px;
    }

    #div_loader {
        border: 4px solid #f3f3f3; /* Light grey */
        border-top: 4px solid #3498db; /* Blue */
        border-radius: 50%;

        position: fixed;
        bottom: 0;
        right: 0;
        width: 30px;
        height: 30px;
     
        animation: spin 0.5s linear infinite;
    }


    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
</style>
<div id='results'></div>
<div id="div_loader" style="display: none"></div>

    """
) {
    companion object {
        val shared by lazy { LoaderWidget() }
    }

    private val div_loader: HTMLElement by this
    private val results: HTMLDivElement by this
    var visible by forward { div_loader::extVisible }
    private var counter = 0

    fun spinner(function: suspend CoroutineScope.() -> Unit) {
        counter++
        if (counter == 0)
            visible = true

        window.requestAnimationFrame {
            window.setTimeout({
                launchJs {
                    kotlin.runCatching {
                        function()
                    }.exceptionOrNull()?.apply {
                        console.log("runcatching", this)
                    }
                    visible = false
                }.apply {

                }
            })
        }
    }

    private var log: String = ""
        set(value) {
            console.log(value)
            results.innerHTML = value
        }
}


