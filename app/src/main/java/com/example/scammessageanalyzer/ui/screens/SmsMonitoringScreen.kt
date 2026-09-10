package com.example.scammessageanalyzer.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.scammessageanalyzer.data.sms.hasSmsAccessPermissions
import com.example.scammessageanalyzer.data.sms.smsPermissions
import com.example.scammessageanalyzer.ui.components.AppGradientBackground
import com.example.scammessageanalyzer.ui.components.ScreenIntroCard
import com.example.scammessageanalyzer.ui.components.SmsMonitoringSection
import com.example.scammessageanalyzer.viewmodel.AnalyzerUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsMonitoringScreen(
    uiState: AnalyzerUiState,
    onSmsAccessChange: (Boolean) -> Unit,
    onScanLatestSmsClick: () -> Unit,
    onNavigateToPaste: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        onSmsAccessChange(context.hasSmsAccessPermissions())
    }

    DisposableEffect(context, lifecycleOwner) {
        onSmsAccessChange(context.hasSmsAccessPermissions())

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onSmsAccessChange(context.hasSmsAccessPermissions())
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AppGradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Inbox Scan") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    actions = {
                        TextButton(onClick = onNavigateToPaste) {
                            Text("Paste")
                        }
                        FilledTonalIconButton(onClick = onNavigateToHistory) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "View incident history"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                ScreenIntroCard(
                    eyebrow = "On-device SMS",
                    title = "Review recent inbox messages without leaving the app.",
                    description = "Older SMS are scanned only when you tap the button, while new incoming messages can still be checked automatically."
                )
                Spacer(modifier = Modifier.height(18.dp))
                SmsMonitoringSection(
                    hasSmsAccess = uiState.hasSmsAccess,
                    isScanningSms = uiState.isScanningSms,
                    smsScanSummary = uiState.smsScanSummary,
                    onGrantAccessClick = { permissionLauncher.launch(smsPermissions) },
                    onScanLatestSmsClick = onScanLatestSmsClick
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
