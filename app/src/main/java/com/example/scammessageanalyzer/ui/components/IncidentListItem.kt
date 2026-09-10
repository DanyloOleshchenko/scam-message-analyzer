package com.example.scammessageanalyzer.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.scammessageanalyzer.data.local.IncidentEntity
import com.example.scammessageanalyzer.domain.logic.ScamAnalyzer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IncidentListItem(
    incident: IncidentEntity,
    onMarkAsSafeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tone = riskToneFor(incident.riskLevel)
    var isExpanded by rememberSaveable(incident.id) { mutableStateOf(false) }
    val analysis = remember(incident.originalMessage, incident.riskLevel, isExpanded) {
        if (incident.riskLevel != "Safe" && isExpanded) {
            ScamAnalyzer.analyze(incident.originalMessage)
        } else {
            null
        }
    }
    val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        .format(Date(incident.timestamp))
    val sourceLabel = when {
        incident.sourceType == IncidentEntity.SOURCE_SMS && !incident.sender.isNullOrBlank() ->
            "SMS from ${incident.sender}"

        incident.sourceType == IncidentEntity.SOURCE_SMS -> "SMS"
        else -> "Manual check"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = tone.containerColor.copy(alpha = 0.94f)),
        border = BorderStroke(1.dp, tone.borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                AppStatusPill(
                    text = incident.riskLevel,
                    containerColor = tone.accentColor,
                    contentColor = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = sourceLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = incident.originalMessage,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            AppStatusPill(
                text = "Score ${incident.riskScore}",
                containerColor = tone.accentColor.copy(alpha = 0.12f),
                contentColor = tone.accentColor
            )
            if (incident.riskLevel != "Safe") {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    border = BorderStroke(1.dp, tone.accentColor.copy(alpha = 0.72f))
                ) {
                    Text(if (isExpanded) "Hide score breakdown" else "Break down the score")
                }
            }
            if (isExpanded && analysis != null) {
                Spacer(modifier = Modifier.height(4.dp))
                AnalysisBreakdownSection(analysis = analysis)
            }
            if (incident.riskLevel != "Safe") {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onMarkAsSafeClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Text("Mark as safe")
                    }
                    OutlinedButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.72f))
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
