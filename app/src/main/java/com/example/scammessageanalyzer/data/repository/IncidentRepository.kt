package com.example.scammessageanalyzer.data.repository

import com.example.scammessageanalyzer.data.local.IncidentDao
import com.example.scammessageanalyzer.data.local.IncidentEntity
import com.example.scammessageanalyzer.data.sms.SmsInboxDataSource
import com.example.scammessageanalyzer.data.sms.SmsScanSummary
import com.example.scammessageanalyzer.domain.logic.ScamAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class IncidentRepository(
    private val dao: IncidentDao,
    private val smsInboxDataSource: SmsInboxDataSource
) {

    companion object {
        // Receiver and inbox timestamps can drift, especially on emulators.
        private const val SMS_DUPLICATE_TIME_WINDOW_MS = 90 * 60 * 1000L
    }

    val allIncidents: Flow<List<IncidentEntity>> = dao.getAllIncidents()

    suspend fun insert(incident: IncidentEntity): Long =
        withContext(Dispatchers.IO) {
            dao.insertIncident(incident)
        }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dao.deleteAllIncidents()
        }
    }

    suspend fun dismissIncident(incidentId: Int) {
        withContext(Dispatchers.IO) {
            dao.dismissIncident(incidentId)
        }
    }

    suspend fun deleteIncident(incidentId: Int) {
        withContext(Dispatchers.IO) {
            dao.deleteIncident(incidentId)
        }
    }

    suspend fun scanRecentSms(limit: Int = 25): SmsScanSummary =
        withContext(Dispatchers.IO) {
            val messages = smsInboxDataSource.getRecentMessages(limit)
            var flaggedCount = 0
            var newIncidentsSaved = 0

            messages.forEach { message ->
                val wasSaved = saveSmsIncidentIfRisky(
                    body = message.body,
                    sender = message.sender,
                    sourceMessageId = message.sourceMessageId,
                    receivedAt = message.receivedAt
                )

                if (wasSaved != null) {
                    flaggedCount += 1
                    if (wasSaved) {
                        newIncidentsSaved += 1
                    }
                }
            }

            SmsScanSummary(
                scannedCount = messages.size,
                flaggedCount = flaggedCount,
                newIncidentsSaved = newIncidentsSaved
            )
        }

    suspend fun recordIncomingSms(
        body: String,
        sender: String?,
        sourceMessageId: String,
        receivedAt: Long
    ): Boolean =
        withContext(Dispatchers.IO) {
            saveSmsIncidentIfRisky(
                body = body,
                sender = sender,
                sourceMessageId = sourceMessageId,
                receivedAt = receivedAt
            ) == true
        }

    private fun saveSmsIncidentIfRisky(
        body: String,
        sender: String?,
        sourceMessageId: String?,
        receivedAt: Long
    ): Boolean? {
        val cleanedBody = body.trim()
        if (cleanedBody.isBlank()) return null

        val result = ScamAnalyzer.analyze(cleanedBody)
        if (result.riskLevel == "Safe") return null

        val alreadySaved = dao.hasIncidentBySmsIdentity(
            sourceType = IncidentEntity.SOURCE_SMS,
            message = cleanedBody,
            timestamp = receivedAt,
            sender = sender,
            timestampToleranceMs = SMS_DUPLICATE_TIME_WINDOW_MS
        )
        if (alreadySaved) return false

        val incidentId = dao.insertIncident(
            IncidentEntity(
                originalMessage = cleanedBody,
                riskLevel = result.riskLevel,
                riskScore = result.riskScore,
                reasons = result.reasons.joinToString(", "),
                timestamp = receivedAt,
                sourceType = IncidentEntity.SOURCE_SMS,
                sender = sender,
                sourceMessageId = sourceMessageId
            )
        )

        return incidentId != -1L
    }
}
