package com.example.scammessageanalyzer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.scammessageanalyzer.data.local.IncidentEntity
import com.example.scammessageanalyzer.data.repository.IncidentRepository
import com.example.scammessageanalyzer.domain.logic.ScamAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnalyzerViewModel(private val repository: IncidentRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyzerUiState())
    val uiState: StateFlow<AnalyzerUiState> = _uiState.asStateFlow()

    val incidentHistory = repository.allIncidents

    fun onInputTextChange(newText: String) {
        _uiState.update { it.copy(inputText = newText) }
    }

    fun analyzeAndSave() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return

        val result = ScamAnalyzer.analyze(text)
        _uiState.update { it.copy(result = result, isSaving = true) }

        viewModelScope.launch {
            repository.insert(
                IncidentEntity(
                    originalMessage = text,
                    riskLevel = result.riskLevel,
                    riskScore = result.riskScore,
                    reasons = result.reasons.joinToString(", "),
                    timestamp = System.currentTimeMillis()
                )
            )
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    fun clearInput() {
        _uiState.update {
            it.copy(
                inputText = "",
                result = null,
                isSaving = false
            )
        }
    }

    fun setSmsAccessGranted(isGranted: Boolean) {
        _uiState.update { it.copy(hasSmsAccess = isGranted) }
    }

    fun scanRecentSms(limit: Int = 25) {
        val currentState = _uiState.value
        if (!currentState.hasSmsAccess || currentState.isScanningSms) return

        _uiState.update { it.copy(isScanningSms = true) }

        viewModelScope.launch {
            val summary = repository.scanRecentSms(limit)
            _uiState.update {
                it.copy(
                    isScanningSms = false,
                    smsScanSummary = summary
                )
            }
        }
    }

    fun dismissIncident(incidentId: Int) {
        viewModelScope.launch {
            repository.dismissIncident(incidentId)
        }
    }

    fun deleteIncident(incidentId: Int) {
        viewModelScope.launch {
            repository.deleteIncident(incidentId)
        }
    }

    fun deleteAllIncidents() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}

class AnalyzerViewModelFactory(
    private val repository: IncidentRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(AnalyzerViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return AnalyzerViewModel(repository) as T
    }
}
