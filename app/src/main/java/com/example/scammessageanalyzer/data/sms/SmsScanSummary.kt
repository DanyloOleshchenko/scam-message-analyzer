package com.example.scammessageanalyzer.data.sms

data class SmsScanSummary(
    val scannedCount: Int,
    val flaggedCount: Int,
    val newIncidentsSaved: Int
) {
    val duplicateCount: Int
        get() = flaggedCount - newIncidentsSaved
}
