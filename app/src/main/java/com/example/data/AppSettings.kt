package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val seniorName: String = "پدربزرگ عزیز",
    val seniorPhone: String = "09123456789",
    val caregiverName: String = "فرزند / پرستار",
    val caregiverPhone: String = "09129876543",
    val currentRole: String = "ELDERLY", // "ELDERLY" or "CAREGIVER"
    val alertTimeoutMinutes: Int = 30,
    val ttsEnabled: Boolean = true,
    val pinCode: String = "1234",
    val autoReportEnabled: Boolean = true
)
