package com.example.project_ellen_kotlin.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_ellen_kotlin.Receipt

class SharedViewModel : ViewModel() {

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> = _imageUri

    val listOfReceipts = mutableListOf<Receipt>()

    fun addReceipt(receipt: Receipt) {
        // do something
        listOfReceipts.add(receipt)
    }

    fun retrieveListOfReceipts() : List<Receipt> {
        return listOfReceipts
    }
    fun removeReceipt(receipt: Receipt) { TODO() }
    fun updateImageUri(uri: Uri) {
        _imageUri.value = uri
    }

}