package grid

import extensions.style
import kotlinx.browser.document
import org.w3c.dom.HTMLTableElement


fun HTMLTableElement.ensureCssStyle() {
    classList.add(cssSelector)
    val head = document.head ?: return
    val id = "${cssSelector}_id"
    val element = head.querySelector("#$id")
    if (element != null) return

    head.append(style().also {
        it.id = id
        it.innerHTML = css
    })
}

private val cssSelector = "components_grid_Grid"
private val css =//language=CSS
    """
     .$cssSelector {
        background-color: white;
        font-weight: 400;
        border-collapse: collapse;
        border-radius: 1em;
        overflow: hidden;
    }
    .$cssSelector th {
        background-color: #3E4D65;
        color: white;
        height: 2em;
        text-align: start;
        padding-left: 0.5em;
        padding-right: 0.5em;
        font-weight: normal;
    }
    .$cssSelector td {
        vertical-align: middle;
        padding-left: 0.5em;
        padding-right: 0.5em;
    }
    .$cssSelector .toolbar {
        font-size: 1.2em;
        padding: 0.5em 0.2em 0.3em 0.8em;
        cursor: pointer;
    }
""".trimIndent()