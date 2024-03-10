package controller

import model.MenuTable
import repository.Database
import view.PrintMenu
import view.PrintSelect
import kotlinx.coroutines.*
import model.Order
import model.OrderTable
import java.lang.IndexOutOfBoundsException

class ForthStep(ps: PrintSelect, rd: Reader, db: Database, role: String,
                third: ThirdStep, value: String, dishes: String = "", id: String = "",
    time: Int = 0, price: Int = 0, name: String, first: String = ""): Step(ps, rd, db, role) {
    private var value: String
    private val third: ThirdStep
    private var dishes: String
    private val id: String
    private val time: Int
    private val price: Int
    private var name: String
    private var first: String

    init {
        this.value = value
        this.third = third
        this.dishes = dishes
        this.id = id
        this.time = time
        this.price = price
        this.name = name
        this.first = first
        if (role == "User" && value != "") {
            this.dishes += "$value;"
            MenuTable(db).update(
                2,
                (MenuTable(db).select(2, 2, 1, value)[0].split("|")[2].toInt() - 1).toString(),
                value
            )
        }
    }

    override fun start() {
        val menu =  if (role == "Admin") {
            Menu(
                listOf(1, 2, 3, 4, 0), listOf(
                    Pair("change $value time") { update(2) },
                    Pair("change $value count") { update(0) },
                    Pair("change $value price") { update(1) },
                    Pair("delete $value") { delete() },
                    Pair("back") { third.start() })
            )
        } else {
            Menu(
                listOf(1, 2, 0), listOf(
                    Pair("add one more dish") { third.chooseDish(dishes, id, 0, price, time, first) },
                    Pair("finish") { finish() },
                    Pair("back") { back() })
            )
        }
        PrintMenu(menu).printMenu()
        MakeChoice(menu).makeChoice()
    }

    private fun update(type: Int) {
        val attribute = rd.readDishAttribute(type)
        MenuTable(db).update(2 + type, attribute.toString(), value)
        start()
    }

    private fun delete() {
        MenuTable(db).delete(value)
        third.start()
    }

    private fun back() {
        val arrayOne = first.split(";")
        val arrayTwo = dishes.split(";")
        for (i in 0..< arrayTwo.count()) {
            try {
                if (arrayOne[i] == arrayTwo[i]) {
                    continue
                }
            } catch (_: IndexOutOfBoundsException) {
            }
            if (arrayTwo[i].isNotEmpty()) {
                MenuTable(db).update(
                    2,
                    (MenuTable(db).select(2, 2, 1, arrayTwo[i])[0].split("|")[2].toInt() + 1).toString(),
                    arrayTwo[i]
                )
            }
        }
        third.start()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun finish() {
        if (id.isEmpty()) {
            val order = Order(dishes, price, time, name, "принят")
            runBlocking {
                OrderTable(db).insert(order)
                val elms = OrderTable(db).select(2, 2, 1, dishes).toMutableList()
                elms.removeIf { it.split("|")[3] != name }
                val orderId = elms[0].split("|")[0]

                val asyncTask = async {
                    third.start()
                }

                val job = GlobalScope.launch {
                    makeOrder(orderId)
                }

                asyncTask.await()
                job.join()
            }
        } else {
            runBlocking {
                OrderTable(db).update(1, dishes, id)
                OrderTable(db).update(2, price.toString(), id)
                third.start()
            }
        }
    }

    private suspend fun makeOrder(orderId: String) {
        coroutineScope {
            launch {
                delay(30000)
                OrderTable(db).update(4, "готовится", orderId)
            }
            launch {
                delay((time * 60000).toLong())
                OrderTable(db).update(4, "готов", orderId)
            }
        }
    }
}