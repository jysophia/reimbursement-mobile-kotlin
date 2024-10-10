package com.example.project_ellen_kotlin

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import java.time.LocalDate

data class Receipt(val newDate: Any, val newPrice: Double, val newPurpose: String, val newUri: Uri?) {
    private var date : Any? = null
    private var price : Double = 0.0
    private var purpose : String = ""
    private var uri : Uri? = null
    private var imageData : Bitmap? = null

    fun setPurpose(updatedPurpose: String) {
        purpose = updatedPurpose
    }

    fun getPurpose(): String {
        return purpose
    }

    fun setDate(updatedDate: Any) {
        date = updatedDate
    }

    fun getDate(): Any? {
        return date
    }

    fun setPrice(updatedPrice: Double) {
        price = updatedPrice
    }

    fun getPrice() : Double {
        return price
    }

    fun setUri(updatedUri : Uri) {
        uri = updatedUri
    }

    fun getUri() : Uri? {
        return uri
    }

    fun setImageData(bitmap : Bitmap) {
        imageData = bitmap
    }

    fun getImageData() : Bitmap? {
        return imageData
    }

}
