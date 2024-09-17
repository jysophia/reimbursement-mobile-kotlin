package com.example.project_ellen_kotlin

data class Email(val attachReceipt: Receipt?, val recipientEmail: String, val subjectLine: String, val emailMessage: String) {
    private var attachment : Receipt? = null
    private var recipient: String = ""
    private var subject: String = ""
    private var message : String = ""

    constructor() : this(null, "", "", "")

    fun setAttachment(receipt: Receipt) {
        attachment = receipt
    }

    fun getAttachment() : Receipt? {
        return attachment
    }

    fun setRecipient(recipientEmail: String) {
        recipient = recipientEmail
    }

    fun getRecipient() : String {
        return recipient
    }

    fun setSubjectLine(subjectLine: String) {
        subject = subject
    }

    fun getSubject() : String {
        return subject
    }

    fun setMessage(msg : String) {
        message = msg
    }

    fun getMessage() : String {
        return message
    }
}