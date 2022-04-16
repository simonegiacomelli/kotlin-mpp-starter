package dom

import kotlinx.coroutines.CoroutineScope
import pages.LoaderWidget

fun spinner(function: suspend CoroutineScope.() -> Unit) = LoaderWidget.shared.spinner(function)