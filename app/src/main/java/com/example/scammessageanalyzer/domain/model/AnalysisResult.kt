package com.example.scammessageanalyzer.domain.model

data class AnalysisResult(
    val riskLevel: String,
    val riskScore: Int,
    val reasons: List<String>,
    val scoreBreakdown: List<ScoreBreakdownItem> = emptyList(),
    val linkAnalyses: List<LinkAnalysisResult> = emptyList()
)

data class ScoreBreakdownItem(
    val title: String,
    val points: Int,
    val detail: String
)

data class LinkAnalysisResult(
    val originalUrl: String,
    val displayHost: String,
    val riskLevel: String,
    val riskScore: Int,
    val findings: List<String>
)
