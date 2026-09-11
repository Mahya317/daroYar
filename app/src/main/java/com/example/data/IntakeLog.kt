package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "intake_logs")
data class IntakeLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicationId: Long,
    val medicationName: String,
    val dosage: String,
    val scheduledTime: String, // e.g. "08:00"
    val dateString: String, // e.g. "2026-08-11"
    val status: String, // TAKEN, MISSED, SNOOZED, PENDING, CAREGIVER_ALERTED
    val reason: String? = null, // e.g. "درد معده", "فراموش کردم", "نخواستم"
    val actionTime: Long? = null, // timestamp when action was taken
    val scheduledTimestamp: Long = System.currentTimeMillis()
)
