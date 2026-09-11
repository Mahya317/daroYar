package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppSettings
import com.example.data.IntakeLog
import com.example.ui.theme.*
import com.example.util.PersianUtils

@Composable
fun SeniorDashboard(
    settings: AppSettings,
    dueLog: IntakeLog?,
    todayLogs: List<IntakeLog>,
    onConfirmTaken: (Long) -> Unit,
    onConfirmMissed: (Long, String) -> Unit,
    onConfirmSnooze: (Long) -> Unit,
    onSpeakRequested: () -> Unit
) {
    var showMissedReasonDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SeniorBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Welcome Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SeniorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(TealContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WavingHand,
                            contentDescription = "Greeting",
                            tint = OnTealContainer,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "سلام ${settings.seniorName} عزیز! 👋",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = SeniorTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "برنامه دارویی امروز شما:",
                            fontSize = 16.sp,
                            color = SeniorTextSecondary
                        )
                    }
                }
            }
        }

        // Active Due Medication Card OR Celebration Card
        item {
            if (dueLog != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SeniorAmberContainer),
                    border = CardDefaults.outlinedCardBorder().copy(width = 3.dp, brush = androidx.compose.ui.graphics.SolidColor(SeniorAmberSnooze)),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Badge Alert Time
                        Surface(
                            color = SeniorAmberSnooze,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Clock",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "زمان مصرف: ${PersianUtils.toPersianDigits(dueLog.scheduledTime)}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Medication Name
                        Text(
                            text = dueLog.medicationName,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OnSeniorAmberContainer,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "دوز مصرفی: ${PersianUtils.toPersianDigits(dueLog.dosage)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnSeniorAmberContainer
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Voice Prompt Speaker Button
                        OutlinedButton(
                            onClick = onSpeakRequested,
                            shape = RoundedCornerShape(16.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Voice",
                                tint = OnSeniorAmberContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "🔊 پخش مجدد صدای یادآور",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSeniorAmberContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3 HUGE ACTION BUTTONS
                        // 1. GREEN BUTTON: خوردم (I Took It)
                        Button(
                            onClick = { onConfirmTaken(dueLog.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = SeniorGreenSuccess),
                            shape = RoundedCornerShape(20.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .testTag("btn_took_it")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Took it",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "خوردم",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 2. RED BUTTON: نخوردم (I Didn't Take It)
                            Button(
                                onClick = { showMissedReasonDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SeniorRedAlert),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .testTag("btn_did_not_take")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = "Didn't take",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "نخوردم",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // 3. AMBER BUTTON: یادآوری بعد (Remind Later)
                            Button(
                                onClick = { onConfirmSnooze(dueLog.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = SeniorAmberSnooze),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(64.dp)
                                    .testTag("btn_remind_later")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Snooze,
                                        contentDescription = "Snooze",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "یادآوری بعد",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // All clear celebration card
                Card(
                    colors = CardDefaults.cardColors(containerColor = SeniorGreenContainer),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Done",
                            tint = SeniorGreenSuccess,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "عالی! تمام داروهای فعلی مصرف شده‌اند.",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSeniorGreenContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "سلامتی شما آرزوی ماست 🌸",
                            fontSize = 16.sp,
                            color = OnSeniorGreenContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Today's Timeline Summary
        item {
            Text(
                text = "لیست داروی امروز شما:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SeniorTextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (todayLogs.isEmpty()) {
            item {
                Text(
                    text = "دارویی برای امروز ثبت نشده است.",
                    fontSize = 16.sp,
                    color = SeniorTextSecondary
                )
            }
        } else {
            items(todayLogs) { log ->
                SeniorLogItem(log)
            }
        }
    }

    if (showMissedReasonDialog && dueLog != null) {
        MissedReasonDialog(
            medicationName = dueLog.medicationName,
            onDismiss = { showMissedReasonDialog = false },
            onReasonSelected = { reason ->
                showMissedReasonDialog = false
                onConfirmMissed(dueLog.id, reason)
            }
        )
    }
}

@Composable
fun SeniorLogItem(log: IntakeLog) {
    val (statusBg, statusText, statusIcon, iconColor) = when (log.status) {
        "TAKEN" -> Tuple4(SeniorGreenContainer, "مصرف شد", Icons.Default.CheckCircle, SeniorGreenSuccess)
        "MISSED" -> Tuple4(SeniorRedContainer, "مصرف نشد (${log.reason ?: "بدون علت"})", Icons.Default.Cancel, SeniorRedAlert)
        "CAREGIVER_ALERTED" -> Tuple4(SeniorRedContainer, "هشدار به مراقب ارسال شد", Icons.Default.Warning, SeniorRedAlert)
        "SNOOZED" -> Tuple4(SeniorAmberContainer, "یادآوری تکرار می‌شود", Icons.Default.Snooze, SeniorAmberSnooze)
        else -> Tuple4(MaterialTheme.colorScheme.surfaceVariant, "در انتظار مصرف", Icons.Default.HourglassEmpty, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SeniorSurface),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = statusText,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = log.medicationName,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = SeniorTextPrimary
                    )
                    Text(
                        text = "ساعت ${PersianUtils.toPersianDigits(log.scheduledTime)} — دوز: ${PersianUtils.toPersianDigits(log.dosage)}",
                        fontSize = 15.sp,
                        color = SeniorTextSecondary
                    )
                }
            }

            Surface(
                color = statusBg,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = statusText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
