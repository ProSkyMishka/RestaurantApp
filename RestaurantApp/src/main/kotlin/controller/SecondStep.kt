package controller

import model.MenuTable
import model.OrderTable
import model.UsersTable
import repository.Database
import view.PrintMenu
import view.PrintSelect

class SecondStep(ps: PrintSelect, rd: Reader, db: Database, role: String,
                 first: FirstStep, name: String
): Step(ps, rd, db, role){
    private var name: String
    private val first: FirstStep

    init {
        this.first = first
        this.name = name
        this.role = role
    }

    override fun start() {
        val menu: Menu
        if (role == "Admin") {
            menu = Menu(listOf(1, 2, 3, 4, 5, 6, 0), listOf(
                Pair("delete account") { deleteWorker() },
                Pair("show menu") { ThirdStep(ps, rd, db, role, this, name).start() },
                Pair("print workers") { workers() },
                Pair("print users") { users() },
                Pair("show statistics") { showStatistics() },
                Pair("show all orders") { showOrders(0) },
                Pair("back") { first.start() })
            )
        } else {
            menu = Menu(listOf(1, 2, 3, 0), listOf(
                Pair("delete account") { deleteWorker() },
                Pair("show menu") { ThirdStep(ps, rd, db, role, this, name).start() },
                Pair("show your orders") { showOrders(1) },
                Pair("back") { first.start() })
            )
        }
        PrintMenu(menu).printMenu()
        MakeChoice(menu).makeChoice()
    }

    private fun deleteWorker() {
        UsersTable(db).delete(name)
        first.start()
    }

    private fun workers() {
        ps.printUsers(UsersTable(db).select(2, 1, 3, "Admin"), "Admin")
        start()
    }

    private fun users() {
        ps.printUsers(UsersTable(db).select(2, 1, 3, "User"), "User")
        start()
    }

    private fun showOrders(type: Int) {
        if (type == 0) {
            ps.printOrders(OrderTable(db).select(1, 2))
        } else {
            ps.printOrders(OrderTable(db).select(2, 2, 3, name))
        }
        start()
    }

    private fun showStatistics() {
        println("Popularity rating of dishes")
        val orders = OrderTable(db).select(1, 2)
        val dishes: MutableMap<String, Int> = mutableMapOf()
        for (order in orders) {
            val dishArray = order.split("|")[1].split(";")
            for (dish in dishArray) {
                if (dish.isNotEmpty()) {
                    try {
                        dishes[dish] = dishes[dish]!! + 1
                    } catch (e: Exception) {
                        dishes.put(dish, 1)
                    }
                }
            }
        }
        val menu = MenuTable(db).select(1, 1)
        for (dish in menu) {
            val dishName = dish.split("|")[1]
            try {
                dishes[dishName] = dishes[dishName]!!
            } catch (e: Exception) {
                dishes[dishName] = 0
            }
        }
        val newDishes = dishes.entries.sortedByDescending { it.value }

        for (dish in newDishes) {
            println(dish.key)
        }
        start()
    }
}