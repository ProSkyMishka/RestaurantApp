package model

import repository.Database

class MenuTable(db: Database): EntityTable(db) {
    init {
        table = "menu"
        columns = db.getColumns(table)
        this.db = db
    }
}