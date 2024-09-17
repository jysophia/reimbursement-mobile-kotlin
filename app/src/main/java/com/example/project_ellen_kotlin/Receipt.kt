package com.example.project_ellen_kotlin

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import java.time.LocalDate

data class Receipt(val newDate: LocalDate, val newPrice: Double, val newPurpose: String, val newUri: Uri?) {
    private var date : LocalDate? = null
    private var price : Double = 0.0
    private var purpose : String = ""
    private var uri : Uri? = null
    private var imageData : ImageCapture.OutputFileResults? = null

    @RequiresApi(Build.VERSION_CODES.O)
    constructor() : this(LocalDate.now(), 0.0, "", null)

    fun setPurpose(updatedPurpose: String) {
        purpose = updatedPurpose
    }

    fun getPurpose(): String {
        return purpose
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDate(updatedDate: LocalDate) {
        date = updatedDate
    }

    fun getDate(): LocalDate? {
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

    fun setImageData(updatedImageData : ImageCapture.OutputFileResults) {
        imageData = updatedImageData
    }

    fun getImageData() : ImageCapture.OutputFileResults? {
        return imageData
    }

}
