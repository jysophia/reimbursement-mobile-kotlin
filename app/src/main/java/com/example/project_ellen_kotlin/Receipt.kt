package com.example.project_ellen_kotlin

import java.util.Date

data class Receipt(val date : Date, val price : Float, val purpose : String, val uri : String) {
    private val thisDate : Date? = null
    private val thisPrice : Float = 0.0F
    private val thisPurpose : String = ""
    private val thisUri : String = ""

    constructor() : this(Date(), 0.0F, "", "")

}
