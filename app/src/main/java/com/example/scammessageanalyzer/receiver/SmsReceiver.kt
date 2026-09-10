package com.example.scammessageanalyzer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.example.scammessageanalyzer.data.local.AppDatabase
import com.example.scammessageanalyzer.data.repository.IncidentRepository
import com.example.scammessageanalyzer.data.sms.SmsInboxDataSource
import com.example.scammessageanalyzer.data.sms.buildSmsFingerprint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                if (messages.isEmpty()) return@launch

                val body = messages.joinToString(separator = "") {
                    it.messageBody.orEmpty()
                }.trim()
                if (body.isBlank()) return@launch

                val sender = messages.firstOrNull()?.displayOriginatingAddress
                val receivedAt = messages.minOfOrNull { it.timestampMillis }
                    ?: System.currentTimeMillis()
                val sourceMessageId = buildSmsFingerprint(
                    sender = sender,
                    body = body,
                    receivedAt = receivedAt
                )

                val database = AppDatabase.getInstance(appContext)
                val repository = IncidentRepository(
                    dao = database.incidentDao(),
                    smsInboxDataSource = SmsInboxDataSource(appContext)
                )

                repository.recordIncomingSms(
                    body = body,
                    sender = sender,
                    sourceMessageId = sourceMessageId,
                    receivedAt = receivedAt
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}
