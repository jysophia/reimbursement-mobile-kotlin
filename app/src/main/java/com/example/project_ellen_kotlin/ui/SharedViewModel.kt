package com.example.project_ellen_kotlin.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_ellen_kotlin.Receipt
import java.util.Date

class SharedViewModel : ViewModel() {

    private val _receiptList = MutableLiveData<Receipt>().apply {
        value = this.value
    }
    val receiptList: MutableLiveData<Receipt> = _receiptList

    val listOfReceipts = mutableListOf<Receipt>()

    fun addReceipt(receipt: Receipt) {
        // do something
//        listOfReceipts.add(receipt)
//        _receiptList.value = receipt
    }

    fun removeReceipt(receipt: Receipt) { TODO() }

    private val _text = MutableLiveData<String>().apply {
        value = "This ViewModel is shared by all fragments"
    }
    val text: LiveData<String> = _text

//    fun selectReceipt(receipt: Receipt) {
//        mutableReceiptList.value = receipt
//    }
}