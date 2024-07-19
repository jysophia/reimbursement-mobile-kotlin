package com.example.project_ellen_kotlin

import java.util.Date

data class Receipt(val newDate : Date, val newPrice : Float, val newPurpose : String, val newUri : String) {
    private var date : Date? = null
    private var price : Float = 0.0F
    private var purpose : String = ""
    private var uri : String = ""

    constructor() : this(Date(), 0.0F, "", "")

    fun setPurpose(updatedPurpose: String) {
        purpose = updatedPurpose
    }

    fun getPurpose(): String {
        return purpose
    }

}
