package com.example.scammessageanalyzer.ui.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scammessageanalyzer.ui.components.AppGradientBackground
import com.example.scammessageanalyzer.ui.components.MessageInputSection
import com.example.scammessageanalyzer.ui.components.RiskResultCard
import com.example.scammessageanalyzer.ui.components.ScreenIntroCard
import com.example.scammessageanalyzer.viewmodel.AnalyzerUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzerScreen(
    uiState: AnalyzerUiState,
    onInputTextChange: (String) -> Unit,
    onAnalyzeClick: () -> Unit,
    onClearClick: () -> Unit,
    onNavigateToSmsMonitoring: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    AppGradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Manual Check") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    actions = {
                        TextButton(onClick = onNavigateToSmsMonitoring) {
                            Text("SMS")
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
                    eyebrow = "Manual check",
                    title = "Paste a message for an instant risk read.",
                    description = "The app scores urgency, money requests, suspicious links, and other scam signals without leaving your device."
                )
                Spacer(modifier = Modifier.height(18.dp))
                MessageInputSection(
                    inputText = uiState.inputText,
                    onTextChange = onInputTextChange,
                    onAnalyzeClick = onAnalyzeClick,
                    onClearClick = onClearClick
                )
                uiState.result?.let { result ->
                    Spacer(modifier = Modifier.height(18.dp))
                    RiskResultCard(result = result)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
