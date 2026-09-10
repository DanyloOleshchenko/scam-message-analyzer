package com.example.scammessageanalyzer.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scammessageanalyzer.data.sms.SmsScanSummary

@Composable
fun SmsMonitoringSection(
    hasSmsAccess: Boolean,
    isScanningSms: Boolean,
    smsScanSummary: SmsScanSummary?,
    onGrantAccessClick: () -> Unit,
    onScanLatestSmsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SMS Monitoring",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                AppStatusPill(
                    text = if (hasSmsAccess) "Ready" else "Permission needed",
                    containerColor = if (hasSmsAccess) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    } else {
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f)
                    },
                    contentColor = if (hasSmsAccess) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (hasSmsAccess) {
                    "Recent inbox messages can be scanned when you ask for it, while new incoming SMS are still checked automatically on-device."
                } else {
                    "Allow SMS access to scan existing messages and inspect new incoming SMS on this device."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            smsScanSummary?.let { summary ->
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.78f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Last scan",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Checked ${summary.scannedCount} SMS, flagged ${summary.flaggedCount}, saved ${summary.newIncidentsSaved} new incidents.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (summary.duplicateCount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Skipped ${summary.duplicateCount} duplicates already in history.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = if (hasSmsAccess) onScanLatestSmsClick else onGrantAccessClick,
                enabled = !isScanningSms,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = when {
                        isScanningSms -> "Scanning SMS..."
                        hasSmsAccess -> "Scan latest SMS"
                        else -> "Allow SMS access"
                    }
                )
            }
        }
    }
}
