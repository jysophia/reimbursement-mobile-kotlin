package com.example.project_ellen_kotlin

data class Email(val attachReceipt: Receipt) {
    private var attachment : Receipt? = null
    private var message : String = ""

    constructor() : this(Receipt())

    fun setMessage(msg : String) {
        message = msg
    }

    fun getMessage() : String {
        return message
    }
}