package com.example.scammessageanalyzer.data.sms

data class SmsInboxMessage(
    val sourceMessageId: String?,
    val sender: String?,
    val body: String,
    val receivedAt: Long
)
