package com.example.scammessageanalyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scammessageanalyzer.auth.AuthRepository
import com.example.scammessageanalyzer.auth.AuthViewModel
import com.example.scammessageanalyzer.auth.AuthViewModelFactory
import com.example.scammessageanalyzer.data.local.AppDatabase
import com.example.scammessageanalyzer.data.repository.IncidentRepository
import com.example.scammessageanalyzer.data.sms.SmsInboxDataSource
import com.example.scammessageanalyzer.ui.components.AppGradientBackground
import com.example.scammessageanalyzer.ui.screens.AnalyzerScreen
import com.example.scammessageanalyzer.ui.screens.HistoryScreen
import com.example.scammessageanalyzer.ui.screens.LoginScreen
import com.example.scammessageanalyzer.ui.screens.SetupPasswordScreen
import com.example.scammessageanalyzer.ui.screens.SmsMonitoringScreen
import com.example.scammessageanalyzer.ui.theme.ScamMessageAnalyzerTheme
import com.example.scammessageanalyzer.viewmodel.AnalyzerViewModel
import com.example.scammessageanalyzer.viewmodel.AnalyzerViewModelFactory

private const val ROUTE_PASTE = "paste"
private const val ROUTE_SMS = "sms"
private const val ROUTE_HISTORY = "history"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository = AuthRepository(applicationContext)
        val authViewModelFactory = AuthViewModelFactory(authRepository)
        val database = AppDatabase.getInstance(applicationContext)
        val smsInboxDataSource = SmsInboxDataSource(applicationContext)
        val repository = IncidentRepository(database.incidentDao(), smsInboxDataSource)
        val viewModelFactory = AnalyzerViewModelFactory(repository)

        setContent {
            ScamMessageAnalyzerTheme {
                val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
                val authUiState by authViewModel.uiState.collectAsState()
                val analyzerViewModel: AnalyzerViewModel = viewModel(factory = viewModelFactory)
                val analyzerUiState by analyzerViewModel.uiState.collectAsState()
                val incidents by analyzerViewModel.incidentHistory.collectAsState(initial = emptyList())

                when {
                    !authUiState.isInitialized -> {
                        AppGradientBackground {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    !authUiState.isPasswordConfigured -> {
                        SetupPasswordScreen(
                            uiState = authUiState,
                            onPasswordChange = authViewModel::onSetupPasswordChange,
                            onConfirmPasswordChange = authViewModel::onConfirmPasswordChange,
                            onCreatePasswordClick = authViewModel::createPassword
                        )
                    }

                    !authUiState.isAuthenticated -> {
                        LoginScreen(
                            uiState = authUiState,
                            onPasswordChange = authViewModel::onUnlockPasswordChange,
                            onUnlockClick = authViewModel::unlockWithPassword
                        )
                    }

                    else -> {
                        val navController = rememberNavController()

                        NavHost(navController = navController, startDestination = ROUTE_PASTE) {
                            composable(ROUTE_PASTE) {
                                AnalyzerScreen(
                                    uiState = analyzerUiState,
                                    onInputTextChange = analyzerViewModel::onInputTextChange,
                                    onAnalyzeClick = analyzerViewModel::analyzeAndSave,
                                    onClearClick = analyzerViewModel::clearInput,
                                    onNavigateToSmsMonitoring = {
                                        navController.navigate(ROUTE_SMS) {
                                            launchSingleTop = true
                                        }
                                    },
                                    onNavigateToHistory = { navController.navigate(ROUTE_HISTORY) }
                                )
                            }
                            composable(ROUTE_SMS) {
                                SmsMonitoringScreen(
                                    uiState = analyzerUiState,
                                    onSmsAccessChange = analyzerViewModel::setSmsAccessGranted,
                                    onScanLatestSmsClick = analyzerViewModel::scanRecentSms,
                                    onNavigateToPaste = {
                                        navController.navigate(ROUTE_PASTE) {
                                            launchSingleTop = true
                                            popUpTo(ROUTE_PASTE)
                                        }
                                    },
                                    onNavigateToHistory = { navController.navigate(ROUTE_HISTORY) }
                                )
                            }
                            composable(ROUTE_HISTORY) {
                                HistoryScreen(
                                    incidents = incidents,
                                    onMarkAsSafeClick = analyzerViewModel::dismissIncident,
                                    onDeleteClick = analyzerViewModel::deleteIncident,
                                    onDeleteAllClick = analyzerViewModel::deleteAllIncidents,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
