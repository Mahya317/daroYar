package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.NotificationHelper
import com.example.util.PersianUtils
import com.example.util.TtsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = MedicationRepository(
        database.medicationDao(),
        database.intakeLogDao(),
        database.appSettingsDao()
    )

    val notificationHelper = NotificationHelper(application)
    val ttsManager = TtsManager(application)

    val settings: StateFlow<AppSettings> = repository.settingsFlow
        .map { it ?: AppSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    val todayDateStr = PersianUtils.getTodayDateString()

    val todayLogs: StateFlow<List<IntakeLog>> = repository.getLogsForDate(todayDateStr)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMedications: StateFlow<List<Medication>> = repository.activeMedications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLogsHistory: StateFlow<List<IntakeLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active pending log that requires immediate attention from Senior
    val currentDueLog: StateFlow<IntakeLog?> = todayLogs.map { logs ->
        logs.firstOrNull { it.status == "PENDING" || it.status == "SNOOZED" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Caregiver Alert list (Logs where senior didn't confirm after 30 mins)
    val caregiverAlerts: StateFlow<List<IntakeLog>> = todayLogs.map { logs ->
        logs.filter { it.status == "CAREGIVER_ALERTED" || it.status == "MISSED" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedInitialDataIfEmpty()
            start30MinUnconfirmedMonitor()
        }
    }

    private suspend fun start30MinUnconfirmedMonitor() {
        while (true) {
            delay(15000) // Check every 15 seconds
            checkAndTriggerUnconfirmedCaregiverAlerts()
        }
    }

    private suspend fun checkAndTriggerUnconfirmedCaregiverAlerts() {
        val currentLogs = todayLogs.value
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute

        for (log in currentLogs) {
            if (log.status == "PENDING" || log.status == "SNOOZED") {
                val timeParts = log.scheduledTime.split(":")
                if (timeParts.size == 2) {
                    val schedHour = timeParts[0].toIntOrNull() ?: 0
                    val schedMinute = timeParts[1].toIntOrNull() ?: 0
                    val schedTotalMinutes = schedHour * 60 + schedMinute

                    val minutesPassed = currentTotalMinutes - schedTotalMinutes
                    if (minutesPassed >= 30) {
                        // Mark as CAREGIVER_ALERTED
                        repository.recordIntake(log.id, "CAREGIVER_ALERTED", "عدم پاسخ سالمند پس از ۳۰ دقیقه")
                        val currentSettings = settings.value
                        notificationHelper.showCaregiverAlertNotification(
                            seniorName = currentSettings.seniorName,
                            medicationName = log.medicationName,
                            scheduledTime = log.scheduledTime
                        )
                    }
                }
            }
        }
    }

    // Senior Actions
    fun confirmTaken(logId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordIntake(logId, "TAKEN")
            _uiMessage.value = "آفرین! مصرف دارو ثبت شد."
        }
    }

    fun confirmMissed(logId: Long, reason: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordIntake(logId, "MISSED", reason)
            _uiMessage.value = "ثبت شد: عدم مصرف دارو"
        }
    }

    fun confirmSnooze(logId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordIntake(logId, "SNOOZED")
            _uiMessage.value = "یادآوری ۱۰ دقیقه دیگر تکرار خواهد شد."
        }
    }

    fun speakCurrentDueMedication() {
        val due = currentDueLog.value
        if (due != null) {
            ttsManager.speakReminder(due.medicationName, due.dosage)
        } else {
            val nextMed = allMedications.value.firstOrNull()
            if (nextMed != null) {
                ttsManager.speakReminder(nextMed.name, nextMed.dosage)
            }
        }
    }

    // Role Switch
    fun switchRole(newRole: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = settings.value.copy(currentRole = newRole)
            repository.updateSettings(updated)
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSettings(newSettings)
            _uiMessage.value = "تنظیمات با موفقیت ذخیره شد."
        }
    }

    // Caregiver Actions: Add Medication
    fun addNewMedication(name: String, dosage: String, timesOfDay: String, instructions: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (name.isBlank() || dosage.isBlank()) {
                _uiMessage.value = "لطفاً نام دارو و دوز را وارد کنید."
                return@launch
            }
            repository.addMedication(name, dosage, timesOfDay, instructions)
            _uiMessage.value = "داروی جدید اضافه شد."
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMedication(medication)
            _uiMessage.value = "دارو حذف شد."
        }
    }

    // Testing / Simulation helpers
    fun simulateMedicationDueNow() {
        viewModelScope.launch(Dispatchers.IO) {
            val medList = allMedications.value
            val medName = medList.firstOrNull()?.name ?: "آسپرین"
            val dosage = medList.firstOrNull()?.dosage ?: "۱ عدد"

            val sdf = SimpleDateFormat("HH:mm", Locale.US)
            val nowTimeStr = sdf.format(Date())

            val simulatedLog = IntakeLog(
                medicationId = medList.firstOrNull()?.id ?: 1L,
                medicationName = medName,
                dosage = dosage,
                scheduledTime = nowTimeStr,
                dateString = todayDateStr,
                status = "PENDING"
            )
            database.intakeLogDao().insertLog(simulatedLog)
            notificationHelper.showSeniorReminderNotification(medName, dosage)
            ttsManager.speakReminder(medName, dosage)
            _uiMessage.value = "شبیه‌سازی: یادآوری دارو فعال شد!"
        }
    }

    fun simulate30MinCaregiverAlert() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSettings = settings.value
            val medList = allMedications.value
            val medName = medList.firstOrNull()?.name ?: "آسپرین"

            val alertLog = IntakeLog(
                medicationId = medList.firstOrNull()?.id ?: 1L,
                medicationName = medName,
                dosage = "۱ عدد",
                scheduledTime = "08:00",
                dateString = todayDateStr,
                status = "CAREGIVER_ALERTED",
                reason = "تأیید نشد بعد از ۳۰ دقیقه (شبیه‌سازی)"
            )
            database.intakeLogDao().insertLog(alertLog)
            notificationHelper.showCaregiverAlertNotification(
                seniorName = currentSettings.seniorName,
                medicationName = medName,
                scheduledTime = "08:00"
            )
            _uiMessage.value = "شبیه‌سازی: هشدار مراقب صادر شد!"
        }
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
