package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppSettings
import com.example.data.IntakeLog
import com.example.data.Medication
import com.example.ui.theme.*
import com.example.util.PersianUtils

@Composable
fun CaregiverDashboard(
    settings: AppSettings,
    medications: List<Medication>,
    todayLogs: List<IntakeLog>,
    allLogsHistory: List<IntakeLog>,
    caregiverAlerts: List<IntakeLog>,
    onAddMedicationClick: () -> Unit,
    onDeleteMedication: (Medication) -> Unit,
    onUpdateSettings: (AppSettings) -> Unit,
    onSimulateReminder: () -> Unit,
    onSimulateCaregiverAlert: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("مرکز پایش و هشدار", "مدیریت داروها", "گزارش هفتگی", "تنظیمات")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SeniorBackground)
    ) {
        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> CaregiverAlertAndMonitorTab(
                settings = settings,
                caregiverAlerts = caregiverAlerts,
                todayLogs = todayLogs,
                onCallSenior = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${settings.seniorPhone}"))
                    context.startActivity(intent)
                },
                onSendSmsSenior = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${settings.seniorPhone}"))
                    context.startActivity(intent)
                },
                onSimulateReminder = onSimulateReminder,
                onSimulateCaregiverAlert = onSimulateCaregiverAlert
            )
            1 -> CaregiverMedicationManagementTab(
                medications = medications,
                onAddMedicationClick = onAddMedicationClick,
                onDeleteMedication = onDeleteMedication
            )
            2 -> CaregiverWeeklyReportTab(
                logsHistory = allLogsHistory
            )
            3 -> CaregiverSettingsTab(
                settings = settings,
                onSaveSettings = onUpdateSettings
            )
        }
    }
}

@Composable
fun CaregiverAlertAndMonitorTab(
    settings: AppSettings,
    caregiverAlerts: List<IntakeLog>,
    todayLogs: List<IntakeLog>,
    onCallSenior: () -> Unit,
    onSendSmsSenior: () -> Unit,
    onSimulateReminder: () -> Unit,
    onSimulateCaregiverAlert: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 30-Minute Missed Medication Alert Banner
        if (caregiverAlerts.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SeniorRedContainer),
                    border = CardDefaults.outlinedCardBorder().copy(width = 2.dp, brush = androidx.compose.ui.graphics.SolidColor(SeniorRedAlert)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alert",
                                tint = SeniorRedAlert,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "🚨 هشدار فوری به مراقب!",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSeniorRedContainer
                                )
                                Text(
                                    text = "عدم تأیید مصرف پس از ۳۰ دقیقه",
                                    fontSize = 14.sp,
                                    color = OnSeniorRedContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        caregiverAlerts.forEach { log ->
                            Text(
                                text = "• داروی ${log.medicationName} (ساعت ${PersianUtils.toPersianDigits(log.scheduledTime)}) توسط ${settings.seniorName} تأیید نشده است.",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSeniorRedContainer,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onCallSenior,
                                colors = ButtonDefaults.buttonColors(containerColor = SeniorRedAlert),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تماس با سالمند", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onSendSmsSenior,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Sms, contentDescription = "SMS", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ارسال پیامک", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Today's Realtime Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SeniorSurface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "پایش زنده وضعیت امروز ${settings.seniorName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SeniorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (todayLogs.isEmpty()) {
                        Text(
                            text = "هنوز برنامه‌ای برای امروز ثبت نشده است.",
                            fontSize = 15.sp,
                            color = SeniorTextSecondary
                        )
                    } else {
                        val takenCount = todayLogs.count { it.status == "TAKEN" }
                        val totalCount = todayLogs.size
                        val progress = if (totalCount > 0) takenCount.toFloat() / totalCount else 0f

                        Text(
                            text = "میزان مصرف امروز: ${PersianUtils.toPersianDigits(takenCount.toString())} از ${PersianUtils.toPersianDigits(totalCount.toString())} دوز",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = SeniorTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(CircleShape),
                            color = SeniorGreenSuccess,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        // Simulation & Testing Tools Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Test",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "پنل تست و شبیه‌سازی قابلیت‌ها",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "جهت بررسی و ارائه عملکرد یادآوری و هشدار مراقب:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onSimulateReminder,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_test_reminder")
                        ) {
                            Text("⚡ تست یادآوری دارو", fontSize = 13.sp)
                        }

                        Button(
                            onClick = onSimulateCaregiverAlert,
                            colors = ButtonDefaults.buttonColors(containerColor = SeniorRedAlert),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .testTag("btn_test_alert")
                        ) {
                            Text("🚨 تست هشدار ۳۰ دقیقه", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CaregiverMedicationManagementTab(
    medications: List<Medication>,
    onAddMedicationClick: () -> Unit,
    onDeleteMedication: (Medication) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "لیست داروهای ثبت شده",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SeniorTextPrimary
                    )

                    Button(
                        onClick = onAddMedicationClick,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("add_medication_fab")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("افزودن دارو", fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (medications.isEmpty()) {
                item {
                    Text(
                        text = "هیچ دارویی ثبت نشده است. جهت اضافه کردن روی دکمه فوق کلیک کنید.",
                        fontSize = 15.sp,
                        color = SeniorTextSecondary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            } else {
                items(medications) { med ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SeniorSurface),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = med.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SeniorTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "دوز: ${PersianUtils.toPersianDigits(med.dosage)} | زمان‌ها: ${PersianUtils.toPersianDigits(med.timesOfDay)}",
                                    fontSize = 15.sp,
                                    color = SeniorTextSecondary
                                )
                                if (med.instructions.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "توضیحات: ${med.instructions}",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            IconButton(onClick = { onDeleteMedication(med) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = SeniorRedAlert
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CaregiverWeeklyReportTab(logsHistory: List<IntakeLog>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            val totalLogs = logsHistory.size
            val takenCount = logsHistory.count { it.status == "TAKEN" }
            val rate = if (totalLogs > 0) (takenCount * 100 / totalLogs) else 100

            Card(
                colors = CardDefaults.cardColors(containerColor = SeniorSurface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "گزارش هفتگی پایبندی به درمان",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SeniorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "%${PersianUtils.toPersianDigits(rate.toString())}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (rate >= 80) SeniorGreenSuccess else SeniorAmberSnooze
                    )
                    Text(
                        text = if (rate >= 80) "پایبندی عالی دارویی" else "نیاز به پیگیری بیشتر مراقب",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = SeniorTextSecondary
                    )
                }
            }
        }

        item {
            Text(
                text = "تاریخچه سوابق مصرف:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SeniorTextPrimary
            )
        }

        if (logsHistory.isEmpty()) {
            item {
                Text(
                    text = "هنوز سوابقی ثبت نشده است.",
                    fontSize = 15.sp,
                    color = SeniorTextSecondary
                )
            }
        } else {
            items(logsHistory) { log ->
                val dayName = PersianUtils.getPersianDayName(log.dateString)
                val statusText = when (log.status) {
                    "TAKEN" -> "✅ مصرف شد"
                    "MISSED" -> "❌ مصرف نشد (دلیل: ${log.reason ?: "نامشخص"})"
                    "CAREGIVER_ALERTED" -> "🚨 عدم پاسخ بعد از ۳۰ دقیقه"
                    else -> "⏳ در انتظار"
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = SeniorSurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$dayName (${PersianUtils.toPersianDigits(log.dateString)}) — ${log.medicationName}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ساعت ${PersianUtils.toPersianDigits(log.scheduledTime)} — $statusText",
                                fontSize = 14.sp,
                                color = SeniorTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CaregiverSettingsTab(
    settings: AppSettings,
    onSaveSettings: (AppSettings) -> Unit
) {
    var seniorName by remember(settings) { mutableStateOf(settings.seniorName) }
    var seniorPhone by remember(settings) { mutableStateOf(settings.seniorPhone) }
    var caregiverName by remember(settings) { mutableStateOf(settings.caregiverName) }
    var caregiverPhone by remember(settings) { mutableStateOf(settings.caregiverPhone) }
    var pinCode by remember(settings) { mutableStateOf(settings.pinCode) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SeniorSurface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "تنظیمات پروفایل و شماره‌های تماس",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SeniorTextPrimary
                    )

                    OutlinedTextField(
                        value = seniorName,
                        onValueChange = { seniorName = it },
                        label = { Text("نام سالمند") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = seniorPhone,
                        onValueChange = { seniorPhone = it },
                        label = { Text("شماره همراه سالمند") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = caregiverName,
                        onValueChange = { caregiverName = it },
                        label = { Text("نام مراقب") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = caregiverPhone,
                        onValueChange = { caregiverPhone = it },
                        label = { Text("شماره همراه مراقب (دریافت پیامک)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = pinCode,
                        onValueChange = { pinCode = it },
                        label = { Text("رمز ورود به بخش مراقب") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onSaveSettings(
                                settings.copy(
                                    seniorName = seniorName.trim(),
                                    seniorPhone = seniorPhone.trim(),
                                    caregiverName = caregiverName.trim(),
                                    caregiverPhone = caregiverPhone.trim(),
                                    pinCode = pinCode.trim()
                                )
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_settings_button")
                    ) {
                        Text("ذخیره تغییرات تنظیمات", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
