package com.example.project_ellen_kotlin

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri

data class Receipt(val newDate: Any, val newPrice: Double, val newPurpose: String, val newUri: Uri?) {
    private lateinit var date : Any
    private var price : Double = 0.0
    private lateinit var purpose : String
    private lateinit var uri : Uri
    private lateinit var imageData : Bitmap

    fun setPurpose(updatedPurpose: String) {
        purpose = updatedPurpose
    }

    fun getDescription(): String {
        return purpose
    }

    fun setDate(updatedDate: Any) {
        date = updatedDate
    }

    fun getDate(): Any? {
        return date
    }

    fun setCost(updatedPrice: Double) {
        price = updatedPrice
    }

    fun getCost() : Double {
        return price
    }

    fun setUri(updatedUri : Uri) {
        uri = updatedUri
    }

    fun getUri() : Uri {
        return uri
    }

    fun setImageData(bitmap : Bitmap) {
        val matrix = Matrix()
        matrix.postRotate(90F)
        imageData = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
    }

    fun getImageData() : Bitmap {
        return imageData
    }

}
