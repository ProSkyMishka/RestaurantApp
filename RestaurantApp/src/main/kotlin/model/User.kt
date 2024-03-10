package model

import java.math.BigInteger
import java.security.MessageDigest

class User(name: String, password: String, role: String): Entity() {
    private var password: String
    var name: String
    var role: String

    init {
        this.name = name
        this.password = md5(password)
        this.role = role
        values.add(this.name)
        values.add(this.password)
        values.add(this.role)
    }

    private fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    fun check(password: String): Boolean {
        return this.password == password
    }
}