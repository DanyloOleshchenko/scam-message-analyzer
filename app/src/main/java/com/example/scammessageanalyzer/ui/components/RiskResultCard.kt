package com.example.scammessageanalyzer.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scammessageanalyzer.domain.model.AnalysisResult

@Composable
fun RiskResultCard(
    result: AnalysisResult,
    modifier: Modifier = Modifier
) {
    val tone = riskToneFor(result.riskLevel)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = tone.containerColor),
        border = BorderStroke(1.dp, tone.borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppStatusPill(
                    text = result.riskLevel,
                    containerColor = tone.accentColor,
                    contentColor = Color.White
                )
                AppStatusPill(
                    text = "Score ${result.riskScore}",
                    containerColor = tone.accentColor.copy(alpha = 0.12f),
                    contentColor = tone.accentColor
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (result.reasons.isNotEmpty()) {
                    "Why it was flagged"
                } else {
                    "No major scam indicators found"
                },
                style = MaterialTheme.typography.titleSmall,
                color = tone.accentColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (result.reasons.isNotEmpty()) {
                result.reasons.forEach { reason ->
                    Text(
                        text = "- $reason",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                AnalysisBreakdownSection(analysis = result)
            } else {
                Text(
                    text = "This message did not trigger the current keyword and pattern rules.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (result.linkAnalyses.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    AnalysisBreakdownSection(analysis = result)
                }
            }
        }
    }
}
