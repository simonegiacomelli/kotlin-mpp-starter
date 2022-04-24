package forms.login

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import widget.Widget

class SessionCheckWidget : Widget(html)

private val html = //language=HTML
    """
<style>
    .form-body {
        display: flex;
        align-items: center;
        padding-top: 40px;
        padding-bottom: 40px;
    }

    .form-signin {
        width: 100%;
        max-width: 330px;
        padding: 15px;
        margin: auto;
    }


    .top-image {
        object-fit: contain;
        width: 100%;
        height: 150px;
    }
</style>
<div class="form-signin">
    <form>
        <img class="top-image" src="img/login.png" alt="">
        <h1 class="h3 mb-3 fw-normal">Logging in...</h1>

        <p class="mt-5 mb-3 text-muted">&copy; 2021–${Clock.System.now().toLocalDateTime(TimeZone.UTC).year} v0.4.0</p>
    </form>
</div>
""".trimIndent()