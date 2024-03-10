package controller

import model.UsersTable
import repository.Database
import view.PrintMenu
import view.PrintSelect
import kotlin.system.exitProcess

class FirstStep(ps: PrintSelect, rd: Reader, db: Database, role: String = ""): Step(ps, rd, db, role) {
    override fun start() {
        val menu = Menu(
            listOf(1, 2, 3, 4, 0), listOf(
                Pair("registration") { registration("User") },
                Pair("authorisation") { authorisation("User") },
                Pair("Admin authorisation") { authorisation("Admin") },
                Pair("Admin registration") { registration("Admin") },
                Pair("close") { exitProcess(0) })
        )
        PrintMenu(menu).printMenu()
        MakeChoice(menu).makeChoice()
    }

    private fun registration(role: String) {
        println("registration:")
        val worker = rd.readUser(role)
        val usersTable = UsersTable(db)
        val result = usersTable.select(2, 1, 1, worker.name)
        if (result.isNotEmpty()) {
            if (result[0].split('|')[3] == role) {
                if (role == "Admin") {
                    println("Admin with this username already exist")
                } else {
                    println("User with this username already exist")
                }
                start()
            } else if (result[0].split('|')[3] == "Admin") {
                println("Admin does not need to register as a user")
                start()
            } else {
                usersTable.update(3, "Admin", result[0].split('|')[1])
                SecondStep(ps, rd, db, worker.role, this, worker.name).start()
            }
        }
        usersTable.insert(worker)
        SecondStep(ps, rd, db, worker.role, this, worker.name).start()
    }

    private fun authorisation(role: String) {
        println("authorisation:")
        val worker = rd.readUser(role)
        val usersTable = UsersTable(db)
        val result = usersTable.select(2, 1, 1, worker.name)
        if (result.isNotEmpty()) {
            if (role == "Admin" && result[0].split('|')[3] != role) {
                println("You don't have admin account")
                start()
            }
            else if (worker.check(result[0].split('|')[2])) {
                println("All right")
                SecondStep(ps, rd, db, worker.role, this, worker.name).start()
            } else {
                println("Incorrect password")
                start()
            }
        }
        println("Firstly you should register oneself")
        start()
    }
}