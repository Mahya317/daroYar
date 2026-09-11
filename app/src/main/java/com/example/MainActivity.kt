package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.SeniorMedicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SeniorMedicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val settings by viewModel.settings.collectAsStateWithLifecycle()
                    val dueLog by viewModel.currentDueLog.collectAsStateWithLifecycle()
                    val todayLogs by viewModel.todayLogs.collectAsStateWithLifecycle()
                    val allMedications by viewModel.allMedications.collectAsStateWithLifecycle()
                    val allLogsHistory by viewModel.allLogsHistory.collectAsStateWithLifecycle()
                    val caregiverAlerts by viewModel.caregiverAlerts.collectAsStateWithLifecycle()
                    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()

                    val snackbarHostState = remember { SnackbarHostState() }
                    var showPinDialog by remember { mutableStateOf(false) }
                    var pendingRoleSwitch by remember { mutableStateOf<String?>(null) }
                    var showAddMedicationDialog by remember { mutableStateOf(false) }

                    LaunchedEffect(uiMessage) {
                        uiMessage?.let { msg ->
                            snackbarHostState.showSnackbar(msg)
                            viewModel.clearUiMessage()
                        }
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            RoleHeader(
                                settings = settings,
                                onRoleToggleRequested = { targetRole ->
                                    if (targetRole == "CAREGIVER" && settings.currentRole == "ELDERLY") {
                                        pendingRoleSwitch = targetRole
                                        showPinDialog = true
                                    } else {
                                        viewModel.switchRole(targetRole)
                                    }
                                }
                            )
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (settings.currentRole == "ELDERLY") {
                                SeniorDashboard(
                                    settings = settings,
                                    dueLog = dueLog,
                                    todayLogs = todayLogs,
                                    onConfirmTaken = { logId -> viewModel.confirmTaken(logId) },
                                    onConfirmMissed = { logId, reason -> viewModel.confirmMissed(logId, reason) },
                                    onConfirmSnooze = { logId -> viewModel.confirmSnooze(logId) },
                                    onSpeakRequested = { viewModel.speakCurrentDueMedication() }
                                )
                            } else {
                                CaregiverDashboard(
                                    settings = settings,
                                    medications = allMedications,
                                    todayLogs = todayLogs,
                                    allLogsHistory = allLogsHistory,
                                    caregiverAlerts = caregiverAlerts,
                                    onAddMedicationClick = { showAddMedicationDialog = true },
                                    onDeleteMedication = { med -> viewModel.deleteMedication(med) },
                                    onUpdateSettings = { updatedSettings -> viewModel.updateSettings(updatedSettings) },
                                    onSimulateReminder = { viewModel.simulateMedicationDueNow() },
                                    onSimulateCaregiverAlert = { viewModel.simulate30MinCaregiverAlert() }
                                )
                            }
                        }
                    }

                    if (showPinDialog) {
                        PinEntryDialog(
                            correctPin = settings.pinCode,
                            onDismiss = {
                                showPinDialog = false
                                pendingRoleSwitch = null
                            },
                            onSuccess = {
                                showPinDialog = false
                                pendingRoleSwitch?.let { role ->
                                    viewModel.switchRole(role)
                                }
                                pendingRoleSwitch = null
                            }
                        )
                    }

                    if (showAddMedicationDialog) {
                        AddMedicationDialog(
                            onDismiss = { showAddMedicationDialog = false },
                            onAddMedication = { name, dosage, times, instructions ->
                                showAddMedicationDialog = false
                                viewModel.addNewMedication(name, dosage, times, instructions)
                            }
                        )
                    }
                }
            }
        }
    }
}

