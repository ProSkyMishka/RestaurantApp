package controller

import model.*
import repository.Database

class Reader(db: Database) {
    val db: Database

    init {
        this.db = db
    }

    fun readReview(): String {
        var flag = false
        var review = ""
        while (!flag) {
            println("evaluate the dish(from 1 to 5")
            var mark: Int
            try {
                mark = readln().toInt()
                if (mark in 1..5) {
                    flag = true
                    review += "$mark;"
                } else {
                    println("it must be integer from 1 to 5")
                }
            } catch (e: NumberFormatException) {
                println("it must be integer")
            }
        }
        println("write your review")
        review += "${readln()};"
        return review
    }

    fun readDishAttribute(type: Int): ULong {
        var flag = false
        var attribute: ULong = 0u
        val message = when (type) {
            2 -> {
                "time(in min):"
            }
            0 -> {
                "count:"
            }
            else -> {
                "price(руб):"
            }
        }
        while (!flag) {
            println(message)
            try {
                attribute = readln().toULong()
                flag = true
            } catch (e: NumberFormatException) {
                println("it must be integer and great or equal than zero")
            }
        }
        return attribute
    }

    fun readDish(): Dish {
        var name = ""
        var flag = false
        while (!flag) {
            println("Name of dish:")
                name = readln()
            if (name.isNotEmpty()) {
                flag = true
            } else {
                println("it must not be empty")
            }
        }
        return Dish(name, readDishAttribute(0).toInt(), readDishAttribute(1).toInt(), readDishAttribute(2).toInt())
    }

    fun readUser(role: String = ""): User {
        var name = ""
        var flag = false
        while (!flag) {
            println("Name:")
            name = readln()
            if (name.isNotEmpty()) {
                flag = true
            } else {
                println("it must not be empty")
            }
        }
        println("password:")
        val password = readln()
        return User(name, password, role)
    }
}