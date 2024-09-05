package com.example.project_ellen_kotlin.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_ellen_kotlin.Receipt
import java.util.Date

class SharedViewModel : ViewModel() {

    private val _receiptList = MutableLiveData<String>().apply {
        value = ""
    }
    val receiptList : LiveData<String> = _receiptList

    val listOfReceipts = mutableListOf<Receipt>()

    fun addReceipt(receipt: Receipt) {
        // do something
        listOfReceipts.add(receipt)
        _receiptList.value = receipt.getPurpose()
    }

    fun removeReceipt(receipt: Receipt) { TODO() }

}