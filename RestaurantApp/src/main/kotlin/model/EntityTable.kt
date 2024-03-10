package model

import repository.CRUD
import repository.Database

abstract class EntityTable(db: Database): CRUD {
    var table: String = ""
    internal var columns: MutableList<String> = mutableListOf()
    internal var db: Database
    private val resultList: MutableList<String> = mutableListOf()

    init {
        this.db = db
    }

    override fun insert(entity: Entity) {
        val values = entity.values
        var request = "INSERT INTO $table VALUES (NULL"
        var i = 1
        for (elem in values) {
            val column = columns[i].split('_')
            request += if (column.count() == 2 && column[1] == "id") {
                ", (SELECT _id FROM ${column[0]} WHERE ${column[0]}_name = '$elem')"
            } else {
                ", '$elem'"
            }
            ++i
        }
        request += ");"
        make(request, false)
    }

    override fun update(columnNumber: Int, columnValue: String, value: String) {
        var request = "UPDATE $table SET ${columns[columnNumber]} = "
        val column = columns[columnNumber].split('_')
        request += if (column.count() == 2 && column[1] == "id") {
            "(SELECT _id FROM ${column[0]} WHERE ${column[0]}_name = '$columnValue')"
        } else {
            "'$columnValue'"
        }
        request += if (!columns.contains("${table}_name")) {
            " WHERE _id = '$value'"
        } else {
            " WHERE ${table}_name = '$value'"
        }
        make(request, false)
    }

    override fun select(type: Int, order: Int, columnNumber: Int,
                        columnValue: String
    ): MutableList<String> {
        resultList.clear()
        var requestLeft = "SELECT $table._id"
        var requestRight = " FROM $table"
        var columnName = ""
        for (elem in columns) {
            if (elem == "_id") {
                continue
            }
            val column = elem.split('_')
            if (column.count() == 2 && column[1] == "id") {
                requestLeft += ", ${column[0]}.${column[0]}_name"
                requestRight += " JOIN ${column[0]} ON ${column[0]}._id = $table.$elem"
                if (elem == columns[columnNumber]) {
                    columnName = "${column[0]}_name"
                }
            } else {
                requestLeft += ", $table.$elem"
            }
        }
        if (type == 2) {
            requestRight += if (columnName != "") {
                " WHERE $columnName = '$columnValue'"
            } else {
                " WHERE ${columns[columnNumber]} = '$columnValue'"
            }
        }
        val orderStr = if (order == 1) {
            "ASC"
        } else {
            "DESC"
        }
        requestLeft += if (table != "session") {
            "$requestRight ORDER BY $table._id $orderStr"
        } else {
            "$requestRight ORDER BY $table.end $orderStr"
        }
        make(requestLeft, true)
        return resultList
    }

    override fun delete(value: String) {
        val request = if (!columns.contains("${table}_name")) {
            "DELETE FROM $table WHERE _id = '$value'"
        } else {
            "DELETE FROM $table WHERE ${table}_name = '$value'"
        }
        make(request, false)
    }

    private fun make(request: String, bool: Boolean) {
        val result = db.raw(request, bool)
        if (bool) {
            while (result?.next() == true) {
                var row = ""
                for (elem in columns) {
                    val column = elem.split('_')
                    row += if (column.count() == 2 && column[1] == "id" && elem != "_id") {
                        result.getString("${column[0]}_name") + "|"
                    } else {
                        result.getString(elem) + "|"
                    }
                }
                resultList.add(row)
            }
        }
    }
}