package pages.bootstrap

import api.names.ApiSearchRequest
import keyboard.Hotkey
import kotlinx.browser.window
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import pages.LoaderWidget
import pages.search.ResultsWidget
import rpc.Api
import utils.Persist
import utils.launchJs
import widget.Widget
import kotlin.collections.set

class MainWidget : Widget(//language=HTML
    """
        
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
            Api.send(request).hits.also {
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

private val homeStyle = """
<style data-emotion="css cbp6lv" data-s="">.css-cbp6lv{box-sizing:border-box;margin:0;min-width:0;display:block;width:100%;padding:8px;-webkit-appearance:none;-moz-appearance:none;-ms-appearance:none;appearance:none;font-size:inherit;line-height:inherit;border:1px solid;border-radius:default;color:inherit;background-color:transparent;background-color:white;font-family:Inter,-apple-system,BlinkMacSystemFont,Segoe UI,Helvetica,Arial,sans-serif;font-size:14px;line-height:24px;color:#2b353e;border-color:#e0e0e0;border-radius:6px;padding:9px 13px;padding-left:8px;padding-right:8px;padding-top:7px;padding-bottom:7px;border-radius:3px;height:32px;}.css-cbp6lv:focus{outline:none;box-shadow:0px 0px 0px 2px #6366F1;border-color:#2161dc;}.css-cbp6lv:disabled{-webkit-text-fill-color:#616161;}@media screen and (max-width: 420px){.css-cbp6lv::-webkit-input-placeholder{color:transparent;}.css-cbp6lv::-moz-placeholder{color:transparent;}.css-cbp6lv:-ms-input-placeholder{color:transparent;}.css-cbp6lv::placeholder{color:transparent;}}@media screen and (max-width: 420px){.css-cbp6lv:focus::-webkit-input-placeholder{color:#616161;}.css-cbp6lv:focus::-moz-placeholder{color:#616161;}.css-cbp6lv:focus:-ms-input-placeholder{color:#616161;}.css-cbp6lv:focus::placeholder{color:#616161;}}</style>"""
