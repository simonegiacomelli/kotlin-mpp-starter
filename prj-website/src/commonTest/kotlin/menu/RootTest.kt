package menu

import kotlin.test.Test

class RootTest {
    @Test
    fun test_menuHierarchy() {
        fun Menu.logMenu(indent: Int = 0) {
            println(" ".repeat(indent * 2) + caption)
            children.forEach { it.logMenu(indent + 1) }
        }
        menuRoot.logMenu()
    }
}