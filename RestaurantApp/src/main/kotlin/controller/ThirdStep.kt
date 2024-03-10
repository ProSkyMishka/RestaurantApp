package controller

import model.MenuTable
import model.*
import repository.Database
import view.PrintMenu
import view.PrintSelect
import java.lang.Exception

class ThirdStep(ps: PrintSelect, rd: Reader, db: Database, role: String,
                second: SecondStep, name: String
): Step(ps, rd, db, role) {
    private val second: SecondStep
    private val name: String

    init {
        this.second = second
        this.name = name
    }

    override fun start() {
        showMenu()
        val menu: Menu = if (role == "Admin") {
            Menu(
                listOf(1, 2, 0), listOf(
                    Pair("add dish") { addDish() },
                    Pair("choose dish") { chooseDish() },
                    Pair("back") { second.start() })
            )
        } else {
            Menu(
                listOf(1, 2, 3, 4, 0), listOf(
                    Pair("make order") { chooseDish() },
                    Pair("add a dish to an existing order") { chooseOrder(2) },
                    Pair("cancel an order") { chooseOrder(1) },
                    Pair("pay for the order") { chooseOrder(0, true) },
                    Pair("back") { second.start() })
            )
        }
        PrintMenu(menu).printMenu()
        MakeChoice(menu).makeChoice()
    }

    private fun showMenu() {
        if (role == "User") {
            ps.printDishes(MenuTable(db).select(1, 1))
        } else {
            ps.printAllDishes(MenuTable(db).select(1, 1))
        }
    }

    private fun addDish() {
        try {
            MenuTable(db).insert(rd.readDish())
        } catch (e: Exception) {
            println(e)
        }
        start()
    }

    fun chooseDish(dishes: String = "", id: String = "", type: Int = 0, price: Int = 0, time: Int = 0, first: String = "") {
        val values = mutableListOf<Pair<String, () -> Unit>>()
        val keys = mutableListOf<Int>()
        var first_ = first
        if (id.isNotEmpty() && first_.isEmpty()) {
            first_ = dishes
        }
        var i = 1
        for (elem in MenuTable(db).select(1, 1)) {
            if (role == "User" && elem.split('|')[2] == "0") {
                continue
            }
            keys.add(i++)
            val newPrice = price + elem.split('|')[3].toInt()
            val newTime = time + elem.split('|')[4].toInt()
            if (type == 1) {
                values.add(Pair(
                    elem.split('|')[1]
                ) { evaluateDish(elem.split('|')[1]) })
            } else {
                values.add(Pair(
                    elem.split('|')[1]
                ) { ForthStep(ps, rd, db, role, this, elem.split('|')[1], dishes, id, newTime, newPrice, name, first_).start() })
            }
        }
        if (dishes == "") {
            values.add(Pair("back") { start() })
        } else {
            values.add(Pair("back") { ForthStep(ps, rd, db, role, this, "", dishes, id, time, price, name, first_).start() })
        }
        keys.add(0)
        val menu = Menu(keys, values)
        PrintMenu(menu).printMenu()
        MakeChoice(menu).makeChoice()
    }

    private fun chooseOrder(type: Int, pay: Boolean = false) {
        val values = mutableListOf<Pair<String, () -> Unit>>()
        val keys = mutableListOf<Int>()
        var i = 1
        val elms = when (type) {
            0 -> {
                OrderTable(db).select(2, 2, 4, "готов")
            }
            1 -> {
                OrderTable(db).select(1, 2)
            }
            else -> {
                OrderTable(db).select(2, 2, 4, "принят")
            }
        }
        for (elem in elms) {
            if (elem.split('|')[4] == "готов" && !pay) {
                continue
            }
            if (elem.split('|')[4] == "оплачен") {
                continue
            }
            if (elem.split('|')[3] != name) {
                continue
            }
            keys.add(i++)
            when (type) {
                1 -> {
                    values.add(Pair(
                        elem.split('|')[1]
                    ) { deleteOrder(elem.split('|')[0]) })
                }
                2 -> {
                    values.add(Pair(
                        elem.split('|')[1]
                    ) { chooseDish(elem.split('|')[1], elem.split('|')[0], 0, elem.split('|')[2].toInt()) })
                }
                else -> {
                    values.add(Pair(
                        elem.split('|')[1]
                    ) { pay(elem.split('|')[0]) })
                }
            }
        }
        values.add(Pair("back") { start() })
        keys.add(0)
        val menu = Menu(keys, values)
        PrintMenu(menu).printMenu()
        MakeChoice(menu).makeChoice()
    }

    private fun deleteOrder(id: String) {
        for (elem in OrderTable(db).select(2, 2, 0, id)[0].split("|")[1].split(";")) {
            if (elem.isNotEmpty()) {
                MenuTable(db).update(
                    2,
                    (MenuTable(db).select(2, 2, 1, elem)[0].split("|")[2].toInt() + 1).toString(),
                    elem
                )
            }
        }
        OrderTable(db).delete(id)
        start()
    }

    private fun evaluateDish(dish: String) {
        MenuTable(db).update(5, rd.readReview(), dish)
        start()
    }

    private fun pay(id: String) {
        OrderTable(db).update(4, "оплачен", id)
        val menu = Menu(
            listOf(1, 0), listOf(
                Pair("evaluate the dish") { chooseDish("", "", 1) },
                Pair("back") { start() })
        )
        PrintMenu(menu).printMenu()
        MakeChoice(menu).makeChoice()
    }
}