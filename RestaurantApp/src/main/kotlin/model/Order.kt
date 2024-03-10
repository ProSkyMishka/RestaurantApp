package model

import javax.print.attribute.standard.JobOriginatingUserName

class Order(dishes: String, price: Int, time: Int, userName: String, status: String): Entity() {
    private var dishes: String
    private var price: Int
    private var time: Int
    private var status: String
    private var userName: String

    init {
        this.dishes = dishes
        this.price = price
        this.time = time
        this.status = status
        this.userName = userName
        values.add(this.dishes)
        values.add(this.price.toString())
        values.add(this.userName)
        values.add(this.status)
        values.add(this.time.toString())
    }
}