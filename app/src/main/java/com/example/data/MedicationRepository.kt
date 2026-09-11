package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class MedicationRepository(
    private val medicationDao: MedicationDao,
    private val intakeLogDao: IntakeLogDao,
    private val appSettingsDao: AppSettingsDao
) {
    val activeMedications: Flow<List<Medication>> = medicationDao.getAllActiveMedications()
    val allMedications: Flow<List<Medication>> = medicationDao.getAllMedications()
    val settingsFlow: Flow<AppSettings?> = appSettingsDao.getSettingsFlow()
    val allLogs: Flow<List<IntakeLog>> = intakeLogDao.getAllLogs()

    fun getLogsForDate(date: String): Flow<List<IntakeLog>> = intakeLogDao.getLogsForDate(date)

    suspend fun getSettings(): AppSettings {
        return appSettingsDao.getSettings() ?: AppSettings().also { appSettingsDao.saveSettings(it) }
    }

    suspend fun updateSettings(settings: AppSettings) {
        appSettingsDao.saveSettings(settings)
    }

    suspend fun addMedication(name: String, dosage: String, timesOfDay: String, instructions: String): Long {
        val med = Medication(
            name = name.trim(),
            dosage = dosage.trim(),
            timesOfDay = timesOfDay.trim(),
            instructions = instructions.trim()
        )
        val medId = medicationDao.insertMedication(med)
        generateTodayLogsForMedication(med.copy(id = medId))
        return medId
    }

    suspend fun updateMedication(medication: Medication) {
        medicationDao.updateMedication(medication)
    }

    suspend fun deleteMedication(medication: Medication) {
        medicationDao.deleteMedication(medication)
    }

    suspend fun recordIntake(logId: Long, status: String, reason: String? = null) {
        intakeLogDao.updateLogStatus(
            id = logId,
            status = status,
            reason = reason,
            actionTime = System.currentTimeMillis()
        )
    }

    suspend fun seedInitialDataIfEmpty() {
        val settings = appSettingsDao.getSettings()
        if (settings == null) {
            appSettingsDao.saveSettings(AppSettings())
        }

        val todayStr = getCurrentDateString()
        // Check if medications exist
        val db = medicationDao
        // We can generate initial default medications if empty
        val defaultMeds = listOf(
            Medication(name = "آسپرین", dosage = "۱ عدد", timesOfDay = "08:00,20:00", instructions = "بعد از غذا جهت محافظت قلبی"),
            Medication(name = "متفورمین", dosage = "۵۰۰ میلی‌گرم", timesOfDay = "12:00", instructions = "همراه با ناهار"),
            Medication(name = "لوزارتان", dosage = "۲۵ میلی‌گرم", timesOfDay = "08:00", instructions = "صبح ناشتا کنترل فشارخون")
        )

        val existingMeds = mutableListOf<Medication>()
        // Let's insert default meds if database is empty
        for (m in defaultMeds) {
            val id = medicationDao.insertMedication(m)
            existingMeds.add(m.copy(id = id))
        }

        // Generate logs for today
        for (med in existingMeds) {
            generateTodayLogsForMedication(med)
        }
    }

    private suspend fun generateTodayLogsForMedication(medication: Medication) {
        val dateStr = getCurrentDateString()
        val times = medication.timesOfDay.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val logs = times.map { time ->
            IntakeLog(
                medicationId = medication.id,
                medicationName = medication.name,
                dosage = medication.dosage,
                scheduledTime = time,
                dateString = dateStr,
                status = "PENDING"
            )
        }
        intakeLogDao.insertLogs(logs)
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
