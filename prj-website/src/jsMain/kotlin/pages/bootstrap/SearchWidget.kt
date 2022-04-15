package pages.bootstrap

import api.names.ApiSearchRequest
import keyboard.Hotkey
import kotlinx.browser.window
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import pages.LoaderWidget
import pages.search.ResultsWidget
import rpc.send
import utils.Persist
import utils.launchJs
import widget.Widget
import kotlin.collections.set

class SearchWidget : Widget(//language=HTML
    """
        
<div class="mt-2 form-check form-switch">
    <div class="dropdown">
      <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenu2" data-bs-toggle="dropdown" aria-expanded="false">
        Dropdown
      </button>
      <ul class="dropdown-menu" aria-labelledby="dropdownMenu2">
        <li><button class="dropdown-item" type="button">Action</button></li>
        <li><button class="dropdown-item" type="button">Another action</button></li>
        <li><button class="dropdown-item" type="button">Something else here</button></li>
      </ul>
    </div>
    <ul class="dropdown-menu">
      <li><span class="dropdown-item-text">Dropdown item text</span></li>
      <li><a class="dropdown-item" href="#">Action</a></li>
      <li><a class="dropdown-item" href="#">Another action</a></li>
      <li><a class="dropdown-item" href="#">Something else here</a></li>
    </ul>
</div>
<div class="mt-2 form-check form-switch">
    <input class="form-check-input" type="checkbox" id="checkbox1" checked>
    <label class="form-check-label text-nowrap " for="checkbox1">sort by star</label>
</div>
<input id="search" class="form-control form-control-sm mb-3" type="search" placeholder="Search..." >

<div id="results"></div>
    """.trimIndent()
) {
    //    private val span1 by this { HtmlWidget() }
    private val checkbox1: HTMLInputElement by this
    private val search: HTMLInputElement by this
    private val results: HTMLDivElement by this
    private val persist = Persist("home-page")

    override fun afterRender() {
        Hotkey(search).add("Enter") { startSearch() }
        checkbox1.onclick = { startSearch() }

        persist.add(checkbox1, "checkbox1")
            .add(search, "search1")
            .load()

        if (search.value.trim() != "") window.setTimeout({ startSearch() }, 1)
    }

    private fun startSearch() {
        persist.save()
        LoaderWidget.shared.visible = true
        launchJs {

            val request = compileRequest()
            request.send().hits.also {
                results.innerHTML = ""
            }.forEach {
                results.append(ResultsWidget(it).container)
            }
            LoaderWidget.shared.visible = false
        }
    }

    private fun compileRequest(): ApiSearchRequest {
        val queryParams: MutableMap<String, String> = HashMap()
        queryParams["q"] = search.value
//    queryParamMap["fl"] = "id, name"
        if (checkbox1.checked)
            queryParams["sort"] = "stargazer desc"
        queryParams["rows"] = "40"
        queryParams["q.op"] = "AND"
        val request = ApiSearchRequest(queryParams)
        return request
    }
}
