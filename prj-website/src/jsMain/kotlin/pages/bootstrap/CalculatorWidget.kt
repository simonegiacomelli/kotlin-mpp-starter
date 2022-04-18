package pages.bootstrap

import api.names.ApiAddRequest
import org.w3c.dom.HTMLInputElement
import rpc.send
import utils.launchJs
import widget.Widget

class CalculatorWidget : Widget(//language=HTML
    """
<div class="input-group mb-3">
    <span class="input-group-text" id="basic-addon1">A</span>
    <input id='inputA' type="text" class="form-control" placeholder="an integer A" aria-label="Username"
           aria-describedby="basic-addon1">
</div>

<div class="input-group mb-3">
    <span class="input-group-text" id="basic-addon1">B</span>
    <input id='inputB' type="text" class="form-control" placeholder="an integer B" aria-label="Username"
           aria-describedby="basic-addon1">
</div>

<div class="form-floating mb-3">
    <input id='inputResult' type="email" class="form-control" id="floatingInput" placeholder="">
    <label for="floatingInput">A + B =</label>
</div>

<div class="card" >
    <div class="card-body">
        <h5 class="card-title">Good to know!</h5>
<!--        <h6 class="card-subtitle mb-2 text-muted">Card subtitle</h6>-->
        <p class="card-text">When the sum is greater than 42, the server will raise an exception.</p>
        <p>Just for fun!</p>
    </div>
</div>


    """.trimIndent()
) {

    private val inputA: HTMLInputElement by this
    private val inputB: HTMLInputElement by this
    private val inputResult: HTMLInputElement by this

    override fun afterRender() {
        inputA.oninput = { calculate() }
        inputB.oninput = { calculate() }
    }

    private fun calculate() = launchJs {
        inputResult.value = ""
        val result = kotlin.runCatching { listOf(inputA.value.toInt(), inputB.value.toInt()) }
        if (result.isFailure) return@launchJs
        val (a, b) = result.getOrThrow()
        val response = ApiAddRequest(a, b).send()
        inputResult.value = response.result.toString()
    }
}
