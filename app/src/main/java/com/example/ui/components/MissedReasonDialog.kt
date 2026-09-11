package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SeniorRedAlert

@Composable
fun MissedReasonDialog(
    medicationName: String,
    onDismiss: () -> Unit,
    onReasonSelected: (String) -> Unit
) {
    val predefinedReasons = listOf(
        "درد یا عارضه معده",
        "فراموش کردم",
        "تمایل به مصرف نداشتم",
        "دارو تمام شده است"
    )
    var customReason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "علت عدم مصرف $medicationName چیست؟",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SeniorRedAlert
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "لطفاً دلیل خود را انتخاب کنید تا برای مراقب شما ارسال شود:",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                predefinedReasons.forEach { reason ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReasonSelected(reason) }
                    ) {
                        Text(
                            text = reason,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = customReason,
                    onValueChange = { customReason = it },
                    label = { Text("سایر دلایل (اختیاری)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            if (customReason.isNotBlank()) {
                Button(
                    onClick = { onReasonSelected(customReason.trim()) },
                    colors = ButtonDefaults.buttonColors(containerColor = SeniorRedAlert),
                    modifier = Modifier.testTag("submit_reason_button")
                ) {
                    Text("ثبت دلیل", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف", fontSize = 16.sp)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
