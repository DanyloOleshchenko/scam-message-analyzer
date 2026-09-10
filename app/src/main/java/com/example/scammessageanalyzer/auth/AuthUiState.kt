package com.example.scammessageanalyzer.auth

data class AuthUiState(
    val isInitialized: Boolean = false,
    val isPasswordConfigured: Boolean = false,
    val isAuthenticated: Boolean = false,
    val setupPassword: String = "",
    val confirmPassword: String = "",
    val unlockPassword: String = "",
    val errorMessage: String? = null
)
