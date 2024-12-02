package com.example.project_ellen_kotlin.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_ellen_kotlin.Email
import com.example.project_ellen_kotlin.Receipt

class SharedViewModel : ViewModel() {

    val receipts = MutableLiveData<List<Receipt>>()
    val listOfReceipts = mutableListOf<Receipt>()

    private val _emailMessage = MutableLiveData<String>()
    val emailMessage: LiveData<String> = _emailMessage

    fun addReceipt(receipt: Receipt) {
        listOfReceipts.add(receipt)
        receipts.value = listOfReceipts
    }

    fun retrieveListOfReceipts() : List<Receipt> {
        return listOfReceipts
    }
    fun removeReceipt(receipt: Receipt) { TODO() }
    fun updateImageUri(uri: Uri) {
        TODO()
    }

    fun setupEmail(email: Email) {
        _emailMessage.value = email.getMessage()
    }

}