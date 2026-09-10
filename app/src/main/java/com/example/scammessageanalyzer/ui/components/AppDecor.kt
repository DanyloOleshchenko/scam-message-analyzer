package com.example.scammessageanalyzer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.example.scammessageanalyzer.ui.theme.AppBackgroundCoolGlow
import com.example.scammessageanalyzer.ui.theme.AppBackgroundDarkCoolGlow
import com.example.scammessageanalyzer.ui.theme.AppBackgroundDarkWarmGlow
import com.example.scammessageanalyzer.ui.theme.AppBackgroundWarmGlow
import com.example.scammessageanalyzer.ui.theme.HighRiskAccent
import com.example.scammessageanalyzer.ui.theme.HighRiskBorder
import com.example.scammessageanalyzer.ui.theme.HighRiskContainer
import com.example.scammessageanalyzer.ui.theme.SafeAccent
import com.example.scammessageanalyzer.ui.theme.SafeBorder
import com.example.scammessageanalyzer.ui.theme.SafeContainer
import com.example.scammessageanalyzer.ui.theme.SuspiciousAccent
import com.example.scammessageanalyzer.ui.theme.SuspiciousBorder
import com.example.scammessageanalyzer.ui.theme.SuspiciousContainer

data class RiskTone(
    val containerColor: Color,
    val accentColor: Color,
    val borderColor: Color
)

fun riskToneFor(level: String): RiskTone = when (level) {
    "High Risk" -> RiskTone(
        containerColor = HighRiskContainer,
        accentColor = HighRiskAccent,
        borderColor = HighRiskBorder
    )

    "Suspicious" -> RiskTone(
        containerColor = SuspiciousContainer,
        accentColor = SuspiciousAccent,
        borderColor = SuspiciousBorder
    )

    else -> RiskTone(
        containerColor = SafeContainer,
        accentColor = SafeAccent,
        borderColor = SafeBorder
    )
}

@Composable
fun AppGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val backgroundColors = if (isLightTheme) {
        listOf(AppBackgroundWarmGlow.copy(alpha = 0.42f), MaterialTheme.colorScheme.background)
    } else {
        listOf(AppBackgroundDarkWarmGlow.copy(alpha = 0.52f), MaterialTheme.colorScheme.background)
    }
    val leftGlow = if (isLightTheme) {
        AppBackgroundWarmGlow.copy(alpha = 0.34f)
    } else {
        AppBackgroundDarkWarmGlow.copy(alpha = 0.36f)
    }
    val rightGlow = if (isLightTheme) {
        AppBackgroundCoolGlow.copy(alpha = 0.34f)
    } else {
        AppBackgroundDarkCoolGlow.copy(alpha = 0.36f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(backgroundColors))
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = (-90).dp, y = (-70).dp)
                .background(leftGlow, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(210.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .background(rightGlow, CircleShape)
        )
        content()
    }
}

@Composable
fun ScreenIntroCard(
    eyebrow: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = eyebrow.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun AppStatusPill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shape = CircleShape
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}
