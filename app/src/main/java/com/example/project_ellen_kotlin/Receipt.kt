package com.example.project_ellen_kotlin

import androidx.camera.core.ImageCapture
import java.util.Date

data class Receipt(val newDate : Date, val newPrice : Float, val newPurpose : String, val newUri : String) {
    private var date : Date? = null
    private var price : Float = 0.0F
    private var purpose : String = ""
    private var uri : String = ""
    private var imageData : ImageCapture.OutputFileResults? = null

    constructor() : this(Date(), 0.0F, "", "")

    fun setPurpose(updatedPurpose: String) {
        purpose = updatedPurpose
    }

    fun getPurpose(): String {
        return purpose
    }

    fun setDate(updatedDate : Date) {
        date = updatedDate
    }

    fun getDate(): Date? {
        return date
    }

    fun setPrice(updatedPrice : Float) {
        price = updatedPrice
    }

    fun getPrice() : Float {
        return price
    }

    fun setUri(updatedUri : String) {
        uri = updatedUri
    }

    fun getUri() : String {
        return uri
    }

    fun setImageData(updatedImageData : ImageCapture.OutputFileResults) {
        imageData = updatedImageData
    }

    fun getImageData() : ImageCapture.OutputFileResults? {
        return imageData
    }

}
