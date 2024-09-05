package com.example.project_ellen_kotlin

import android.net.Uri
import androidx.camera.core.ImageCapture
import java.util.Date

data class Receipt(val newDate: Date, val newPrice: Float, val newPurpose: String, val newUri: Uri?) {
    private var date : Date? = null
    private var price : Float = 0.0F
    private var purpose : String = ""
    private var uri : Uri? = null
    private var imageData : ImageCapture.OutputFileResults? = null

    constructor() : this(Date(), 0.0F, "", null)

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

    fun setUri(updatedUri : Uri) {
        uri = updatedUri
    }

    fun getUri() : Uri? {
        return uri
    }

    fun setImageData(updatedImageData : ImageCapture.OutputFileResults) {
        imageData = updatedImageData
    }

    fun getImageData() : ImageCapture.OutputFileResults? {
        return imageData
    }

}
