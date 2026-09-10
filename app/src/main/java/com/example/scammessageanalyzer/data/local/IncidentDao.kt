package com.example.scammessageanalyzer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIncident(incident: IncidentEntity): Long

    @Query("SELECT * FROM incident_reports WHERE isDismissed = 0 ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Query("UPDATE incident_reports SET isDismissed = 1 WHERE id = :incidentId")
    fun dismissIncident(incidentId: Int): Int

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM incident_reports
            WHERE sourceType = :sourceType
              AND originalMessage = :message
              AND ABS(timestamp - :timestamp) <= :timestampToleranceMs
              AND (
                    (sender IS NULL AND :sender IS NULL)
                    OR sender = :sender
                  )
        )
        """
    )
    fun hasIncidentBySmsIdentity(
        sourceType: String,
        message: String,
        timestamp: Long,
        sender: String?,
        timestampToleranceMs: Long
    ): Boolean

    @Query("DELETE FROM incident_reports WHERE id = :incidentId")
    fun deleteIncident(incidentId: Int): Int

    @Query("DELETE FROM incident_reports")
    fun deleteAllIncidents(): Int
}
