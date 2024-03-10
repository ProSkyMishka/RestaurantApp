package model

import repository.Database

class OrderTable(db: Database): EntityTable(db) {
    init {
        table = "orders"
        columns = db.getColumns(table)
        this.db = db
    }
}