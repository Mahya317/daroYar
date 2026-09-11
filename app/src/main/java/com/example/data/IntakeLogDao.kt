package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IntakeLogDao {
    @Query("SELECT * FROM intake_logs WHERE dateString = :date ORDER BY scheduledTime ASC")
    fun getLogsForDate(date: String): Flow<List<IntakeLog>>

    @Query("SELECT * FROM intake_logs ORDER BY id DESC LIMIT 100")
    fun getAllLogs(): Flow<List<IntakeLog>>

    @Query("SELECT * FROM intake_logs WHERE status = 'PENDING' OR status = 'SNOOZED'")
    suspend fun getPendingLogs(): List<IntakeLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: IntakeLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<IntakeLog>)

    @Update
    suspend fun updateLog(log: IntakeLog)

    @Query("UPDATE intake_logs SET status = :status, reason = :reason, actionTime = :actionTime WHERE id = :id")
    suspend fun updateLogStatus(id: Long, status: String, reason: String?, actionTime: Long)

    @Query("DELETE FROM intake_logs")
    suspend fun clearAllLogs()
}
