package com.example.scammessageanalyzer.data.sms

import android.content.Context
import android.provider.Telephony

class SmsInboxDataSource(private val context: Context) {

    fun getRecentMessages(limit: Int): List<SmsInboxMessage> {
        val messages = mutableListOf<SmsInboxMessage>()
        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE
        )

        context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            projection,
            null,
            null,
            "${Telephony.Sms.DATE} DESC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(Telephony.Sms._ID)
            val addressIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE)

            while (cursor.moveToNext() && messages.size < limit) {
                val body = if (bodyIndex >= 0) cursor.getString(bodyIndex).orEmpty() else ""
                if (body.isBlank()) continue
                val sender = if (addressIndex >= 0) cursor.getString(addressIndex) else null
                val receivedAt = if (dateIndex >= 0) {
                    cursor.getLong(dateIndex)
                } else {
                    System.currentTimeMillis()
                }

                messages += SmsInboxMessage(
                    sourceMessageId = buildSmsFingerprint(
                        sender = sender,
                        body = body,
                        receivedAt = receivedAt,
                        fallbackId = if (idIndex >= 0) cursor.getLong(idIndex).toString() else null
                    ),
                    sender = sender,
                    body = body,
                    receivedAt = receivedAt
                )
            }
        }

        return messages
    }
}
