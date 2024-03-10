package view

class PrintSelect {
    fun printUsers(result: List<String>, role: String) {
        if (role == "User") {
            println("Users:")
        } else {
            println("Workers:")
        }
        for (elem in result) {
            println(elem.split('|')[1])
        }
    }

    fun printOrders(result: List<String>) {
        println("Orders")
        for (elem in result) {
            val elemCopy = elem.split('|')
            println("dish: ${elemCopy[1]}\t|\tprice: ${elemCopy[2]}руб\t|\tuser: ${elemCopy[3]}\t|\tstatus: ${elemCopy[4]}")
        }
    }

    fun printDishes(result: List<String>) {
        println("Menu:")
        for (elem in result) {
            val elemCopy = elem.split('|')
            if (elemCopy[2].toInt() == 0) {
                continue
            }
            println("dish: ${elemCopy[1]}\t|\tprice: ${elemCopy[3]}руб\t|\ttime: ${elemCopy[4]}min")
        }
    }

    fun printAllDishes(result: List<String>) {
        println("Menu:")
        val empty: MutableList<String> = mutableListOf()
        for (elem in result) {
            val elemCopy = elem.split('|')
            if (elemCopy[2].toInt() == 0) {
                empty.add(elem)
                continue
            }
            println("dish: ${elemCopy[1]}\t|\tcount: ${elemCopy[2]}\t|\tprice: ${elemCopy[3]}руб\t|\ttime: ${elemCopy[4]}min\t|\treviews: ${elemCopy[5]}")
        }
        for (elem in empty) {
            val elemCopy = elem.split('|')
            println("dish: ${elemCopy[1]}\t|\tcount: ${elemCopy[2]}\t|\tprice: ${elemCopy[3]}руб\t|\ttime: ${elemCopy[4]}min\t|\treviews: ${elemCopy[5]}")
        }
    }
}