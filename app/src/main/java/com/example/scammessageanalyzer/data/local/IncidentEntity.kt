package com.example.scammessageanalyzer.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "incident_reports",
    indices = [Index(value = ["sourceType", "sourceMessageId"], unique = true)]
)
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalMessage: String,
    val riskLevel: String,
    val riskScore: Int,
    val reasons: String,
    val timestamp: Long,
    val sourceType: String = SOURCE_MANUAL,
    val sender: String? = null,
    val sourceMessageId: String? = null,
    val isDismissed: Boolean = false
) {
    companion object {
        const val SOURCE_MANUAL = "manual"
        const val SOURCE_SMS = "sms"
    }
}
