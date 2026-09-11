package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_SENIOR_REMINDER = "senior_med_reminder_channel"
        const val CHANNEL_CAREGIVER_ALERT = "caregiver_alert_channel"
    }

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val seniorChannel = NotificationChannel(
                CHANNEL_SENIOR_REMINDER,
                "یادآوری مصرف دارو سالمند",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "نوتیفیکیشن زمان مصرف دارو برای سالمند"
                enableVibration(true)
            }

            val caregiverChannel = NotificationChannel(
                CHANNEL_CAREGIVER_ALERT,
                "اعلان عدم مصرف به مراقب",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "هشدار فوری به مراقب در صورت عدم تأیید مصرف پس از ۳۰ دقیقه"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(seniorChannel)
            notificationManager.createNotificationChannel(caregiverChannel)
        }
    }

    fun showSeniorReminderNotification(medicationName: String, dosage: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_SENIOR_REMINDER)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("⏰ زمان مصرف دارو: $medicationName")
            .setContentText("دوز: $dosage — لطفاً مصرف آن را تأیید کنید.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(medicationName.hashCode(), notification)
    }

    fun showCaregiverAlertNotification(seniorName: String, medicationName: String, scheduledTime: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_CAREGIVER_ALERT)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🚨 هشدار عدم مصرف دارو!")
            .setContentText("$seniorName داروی $medicationName (ساعت $scheduledTime) را پس از ۳۰ دقیقه تأیید نکرده است!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("🚨 هشدار عدم مصرف دارو!\n$seniorName داروی $medicationName (ساعت $scheduledTime) را هنوز تأیید نکرده است. لطفاً پیگیری کنید.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify((seniorName + medicationName + scheduledTime).hashCode(), notification)
    }
}
