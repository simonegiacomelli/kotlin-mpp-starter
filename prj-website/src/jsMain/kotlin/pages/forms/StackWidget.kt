package pages.forms

import widget.Widget

class StackWidget : Widget("") {
    private val stack = mutableListOf<Widget>()

    fun show2(widget: Widget) {
        container.innerHTML = ""
        container.append(widget.container)
        stack.push(widget)
    }


    fun close2() {
        if (stack.size < 2) return
        stack.pop()
        show2(stack.pop()!!)
    }
}

fun <T> MutableList<T>.push(item: T) = this.add(this.count(), item)
fun <T> MutableList<T>.pop(): T? = if (this.isNotEmpty()) this.removeAt(this.count() - 1) else null
fun <T> MutableList<T>.peek(): T? = if (this.isNotEmpty()) this[this.count() - 1] else null