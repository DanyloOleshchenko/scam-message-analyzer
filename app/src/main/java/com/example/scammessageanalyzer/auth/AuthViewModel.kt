package com.example.scammessageanalyzer.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        val hasPassword = repository.isPasswordConfigured()
        _uiState.update {
            it.copy(
                isInitialized = true,
                isPasswordConfigured = hasPassword
            )
        }
    }

    fun onSetupPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                setupPassword = newPassword,
                errorMessage = null
            )
        }
    }

    fun onConfirmPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = newPassword,
                errorMessage = null
            )
        }
    }

    fun onUnlockPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                unlockPassword = newPassword,
                errorMessage = null
            )
        }
    }

    fun createPassword() {
        val state = _uiState.value
        val password = state.setupPassword
        val confirmPassword = state.confirmPassword

        when {
            password.length < MIN_PASSWORD_LENGTH -> {
                _uiState.update {
                    it.copy(errorMessage = "Password must be at least 4 characters.")
                }
            }

            password != confirmPassword -> {
                _uiState.update {
                    it.copy(errorMessage = "Passwords do not match.")
                }
            }

            else -> {
                repository.savePassword(password)
                _uiState.update {
                    AuthUiState(
                        isInitialized = true,
                        isPasswordConfigured = true,
                        isAuthenticated = true
                    )
                }
            }
        }
    }

    fun unlockWithPassword() {
        val password = _uiState.value.unlockPassword
        if (password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Enter your password to continue.")
            }
            return
        }

        val isValid = repository.verifyPassword(password)
        if (isValid) {
            _uiState.update {
                it.copy(
                    isAuthenticated = true,
                    unlockPassword = "",
                    errorMessage = null
                )
            }
        } else {
            _uiState.update {
                it.copy(errorMessage = "Incorrect password.")
            }
        }
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 4
    }
}

class AuthViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return AuthViewModel(repository) as T
    }
}
