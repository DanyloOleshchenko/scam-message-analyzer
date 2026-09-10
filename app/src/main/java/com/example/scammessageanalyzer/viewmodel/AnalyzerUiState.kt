package com.example.scammessageanalyzer.viewmodel

import com.example.scammessageanalyzer.data.sms.SmsScanSummary
import com.example.scammessageanalyzer.domain.model.AnalysisResult

data class AnalyzerUiState(
    val inputText: String = "",
    val result: AnalysisResult? = null,
    val isSaving: Boolean = false,
    val hasSmsAccess: Boolean = false,
    val isScanningSms: Boolean = false,
    val smsScanSummary: SmsScanSummary? = null
)
