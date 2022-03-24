package pages.search

import utils.escapeHTML
import widget.Widget


//use css library foundation?
class ResultsWidget(hit: SolrHit) : Widget( //language=HTML
    """
        <h5> <a href="https://github.com/${hit.id}" target="_blank">${hit.id}</a>
        ⭐ ${hit.stargazer.toString().reversed().chunked(3).joinToString(",").reversed()}
        <br>
        ${(hit.description ?: "").escapeHTML.run { take(500) + if (length > 500) " [...]" else "" }}
        ${hit.urlHtml()}
        </h5>
        
        
    """.trimIndent()
) {
    constructor(hit: String) : this(SolrHit(hit))
}

private fun SolrHit.urlHtml(): String {
    if (url == null) return ""
    return """<br><a href="$url" target="_blank">$url</a>"""
}