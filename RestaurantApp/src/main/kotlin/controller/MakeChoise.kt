package controller

class MakeChoice(menu: Menu) {
    private var menu: Menu

    init {
        this.menu = menu
    }

    fun makeChoice() {
        var flag = false
        var choice: Int
        while (!flag) {
            try {
                choice = readln().toInt()
                if (menu.menu.containsKey(choice)) {
                    menu.menu[choice]?.second?.invoke()
                    flag = true
                } else {
                    println("You don't have such variant")
                }
            } catch (e: NumberFormatException) {
                println("it is not number")
            }
        }
    }
}