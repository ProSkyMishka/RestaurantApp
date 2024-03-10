package controller

import repository.Database
import view.PrintSelect

abstract class Step(ps: PrintSelect, rd: Reader, db: Database, role: String) {
    internal var db: Database
    internal var rd: Reader
    internal var ps: PrintSelect
    internal var role: String

    init {
        this.ps = ps
        this.db = db
        this.rd = rd
        this.role = role
    }

    open fun start() {}
}