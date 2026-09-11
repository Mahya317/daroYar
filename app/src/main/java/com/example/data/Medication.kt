package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // e.g. آسپرین
    val dosage: String, // e.g. ۱ عدد
    val timesOfDay: String, // e.g. "08:00,14:00,20:00"
    val instructions: String = "", // e.g. بعد از غذا
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
