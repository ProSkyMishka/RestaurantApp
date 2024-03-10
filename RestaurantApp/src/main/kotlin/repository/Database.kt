package repository

import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class Database {
    private val jdbcUrl = "jdbc:sqlite:IHW-2.db"
    private var db: Statement? = null

    init {
        try {
            val connection = DriverManager.getConnection(jdbcUrl)
            db = connection?.createStatement()
        }
        catch (e: SQLException) {
            println("Error connecting to SQLite database")
            e.printStackTrace()
        }
    }

    fun raw(request: String, select: Boolean): ResultSet? {
        var result: ResultSet? = null
        try {
            if (select) {
                result = db?.executeQuery(request)
            }
            else {
                db?.executeUpdate(request)
            }
        }
        catch (e: SQLException) {
            println("Error")
            println(e)
        }
        return result
    }

    fun getColumns(table: String): MutableList<String> {
        val columns = mutableListOf<String>()
        val request = "SELECT name FROM pragma_table_info('$table');"
        val result = raw(request, true) ?: return columns
        while (result.next()) {
            val name = result.getString("name")
            columns.add(name)
        }
        return columns
    }
}