package model

class Dish(name: String, count: Int, price: Int, time: Int, feedback: String = ""): Entity() {
    private var name: String
    private var count: Int
    private var price: Int
    private var time: Int
    private var feedback: String = ""

    init {
        this.name = name
        this.count = count
        this.price = price
        this.time = time
        this.feedback = feedback
        values.add(this.name)
        values.add(this.count.toString())
        values.add(this.price.toString())
        values.add(this.time.toString())
        values.add(this.feedback)
    }
}