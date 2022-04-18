package pages.bootstrap

import kotlinx.datetime.LocalDate
import widget.Widget

class DataBindingWidget : Widget(//language=HTML
    """
<h1>data binding modeling/prototyping</h1> 
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
"""
) {
    data class User(var name: String, var age: Int, var birthday: LocalDate)

    override fun afterRender() {
        val kname = User::name
        val kage = User::age
        val kbirthday = User::birthday
    }
}