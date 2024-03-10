package view

import controller.Menu

class PrintMenu(menu: Menu) {
    private var menu: Menu

    init {
        this.menu = menu
    }

    fun printMenu() {
        for (elem in menu.menu) {
            println("${elem.key} - ${elem.value.first}")
        }
        println("Choose one variant")
    }
}