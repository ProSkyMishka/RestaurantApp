package controller

import repository.Database
import view.PrintSelect

fun main() {
    val db = Database()
    val rd = Reader(db)
    val ps = PrintSelect()
    val first = FirstStep(ps, rd, db)
    first.start()
}