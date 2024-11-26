package com.example.project_ellen_kotlin.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_ellen_kotlin.Email
import com.example.project_ellen_kotlin.Receipt

class SharedViewModel : ViewModel() {

    private val _imageUri = MutableLiveData<Uri>()
    val receipt: MutableLiveData<Receipt> by lazy {
        MutableLiveData<Receipt>()
    }

    private val _emailMessage = MutableLiveData<String>()
    val emailMessage: LiveData<String> = _emailMessage

    val listOfReceipts = mutableListOf<Receipt>()

    fun addReceipt(receipt: Receipt) {
        this.receipt.value = receipt
        listOfReceipts.add(receipt)
    }

    fun retrieveListOfReceipts() : List<Receipt> {
        return listOfReceipts
    }
    fun removeReceipt(receipt: Receipt) { TODO() }
    fun updateImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    fun setupEmail(email: Email) {
        _emailMessage.value = email.getMessage()
    }

}