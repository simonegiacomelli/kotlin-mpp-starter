package pages

import coroutine.launch
import coroutine.waitAnimationFrame
import extensions.extVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.w3c.dom.HTMLElement
import utils.forward
import widget.Widget

class SpinnerWidget : Widget(//language=HTML
    """
<style>
    .spinner_base {
        border: 4px solid #f3f3f3; /* Light grey */
        border-top: 4px solid #3498db; /* Blue */
        border-radius: 50%;
        position: fixed;
        width: 30px;
        height: 30px;
        animation: spin 0.5s linear infinite;
    }
    
    #div_loader2 {
        left: 0px;
        top: 0px;
    }

    #div_loader {
        bottom: 0;
        right: 0;
    }

    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
</style>
<div id="div_loader" class="spinner_base" style="display: none"></div>

    """
) {

    private val div_loader: HTMLElement by this
    var visible by forward { div_loader::extVisible }
    private var counter = 0

    fun spinner(function: suspend CoroutineScope.() -> Unit) {
        launch { spinnerSuspend(function) }
    }

    suspend fun <T> spinnerSuspend(function: suspend CoroutineScope.() -> T): T = coroutineScope {
        if (counter == 0)
            visible = true
        counter++
        console.log("after increment $counter")
        waitAnimationFrame()
        try {
            return@coroutineScope function()
        } finally {
            counter--
            console.log("after decrement $counter")
            if (counter == 0)
                visible = false
        }

    }

}


