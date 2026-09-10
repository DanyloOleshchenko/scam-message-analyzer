package com.example.scammessageanalyzer.domain.logic

import com.example.scammessageanalyzer.domain.model.AnalysisResult
import com.example.scammessageanalyzer.domain.model.ScoreBreakdownItem

object ScamAnalyzer {

    private val urgencyPhrases = listOf(
        "urgent", "immediately", "act now", "right now", "hurry", "limited time", "expires today"
    )

    private val rewardPhrases = listOf(
        "winner", "won", "free money", "claim prize", "congratulations", "reward", "gift card",
        "free spins", "bonus spins", "free bonus", "jackpot", "cash bonus", "claim now"
    )

    private val threatPhrases = listOf(
        "account suspended", "account blocked", "penalty", "legal action", "arrested",
        "blocked", "terminated", "deactivated"
    )

    private val dataRequestPhrases = listOf(
        "send your password", "confirm your password", "enter your pin", "bank account number",
        "credit card", "social security", "verify your identity", "confirm bank"
    )

    private val moneyRequestPhrases = listOf(
        "send me money", "send money", "need money", "lend me", "wire transfer",
        "transfer money", "in trouble", "need help", "stuck here", "stranded",
        "please help me", "emergency", "send cash", "pay for me", "i'm in trouble",
        "im in trouble", "help me out", "borrow money", "loan me", "bank transfer",
        "send funds", "transfer funds", "venmo me", "cash app me", "zelle me",
        "western union", "moneygram", "crypto transfer", "bitcoin", "usdt"
    )

    private val linkPressurePhrases = listOf(
        "click here", "tap here", "open the link", "verify now", "check now", "follow this link"
    )

    fun analyze(text: String): AnalysisResult {
        val lowerText = text.lowercase()
        val breakdown = mutableListOf<ScoreBreakdownItem>()

        addPhraseBreakdown(
            breakdown = breakdown,
            text = lowerText,
            phrases = urgencyPhrases,
            points = 15,
            title = "Contains urgency language",
            detailPrefix = "Matched urgency phrases"
        )

        addPhraseBreakdown(
            breakdown = breakdown,
            text = lowerText,
            phrases = rewardPhrases,
            points = 15,
            title = "Contains fake reward or prize language",
            detailPrefix = "Matched reward phrases"
        )

        addPhraseBreakdown(
            breakdown = breakdown,
            text = lowerText,
            phrases = threatPhrases,
            points = 20,
            title = "Contains threat or account suspension language",
            detailPrefix = "Matched threat phrases"
        )

        addPhraseBreakdown(
            breakdown = breakdown,
            text = lowerText,
            phrases = dataRequestPhrases,
            points = 25,
            title = "Requests sensitive personal information",
            detailPrefix = "Matched sensitive-data phrases"
        )

        addPhraseBreakdown(
            breakdown = breakdown,
            text = lowerText,
            phrases = moneyRequestPhrases,
            points = 20,
            title = "Requests a money transfer or emergency financial help",
            detailPrefix = "Matched money-transfer phrases"
        )

        val linkAnalyses = LinkAnalyzer.analyzeText(text)
        val riskyLinks = linkAnalyses.filter { it.riskScore > 0 }
        if (riskyLinks.isNotEmpty()) {
            val points = riskyLinks.sumOf { link ->
                when {
                    link.riskScore >= 25 -> 14
                    link.riskScore >= 10 -> 10
                    else -> 0
                }
            }.coerceAtMost(24)
            val worstLink = riskyLinks.maxBy { it.riskScore }
            breakdown += ScoreBreakdownItem(
                title = "Contains suspicious or shortened link",
                points = points,
                detail = "Flagged ${riskyLinks.size} link(s); highest-risk host: ${worstLink.displayHost}."
            )
        } else {
            addPhraseBreakdown(
                breakdown = breakdown,
                text = lowerText,
                phrases = linkPressurePhrases,
                points = 8,
                title = "Pushes the reader toward a link or quick action",
                detailPrefix = "Matched call-to-action phrases"
            )
        }

        val exclamationCount = text.count { it == '!' }
        if (exclamationCount >= 3) {
            breakdown += ScoreBreakdownItem(
                title = "Excessive use of exclamation marks",
                points = 5,
                detail = "Found $exclamationCount exclamation marks."
            )
        }

        val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        val capsWords = words.filter { word ->
            val lettersOnly = word.filter(Char::isLetter)
            lettersOnly.length > 2 && lettersOnly == lettersOnly.uppercase()
        }
        if (capsWords.size >= 3) {
            breakdown += ScoreBreakdownItem(
                title = "Excessive use of capital letters",
                points = 5,
                detail = "Found ${capsWords.size} emphasized uppercase words."
            )
        }

        val score = breakdown.sumOf { it.points }
        val riskLevel = when {
            score >= 45 -> "High Risk"
            score >= 20 -> "Suspicious"
            else -> "Safe"
        }

        return AnalysisResult(
            riskLevel = riskLevel,
            riskScore = score,
            reasons = breakdown.map { it.title },
            scoreBreakdown = breakdown,
            linkAnalyses = linkAnalyses
        )
    }

    private fun addPhraseBreakdown(
        breakdown: MutableList<ScoreBreakdownItem>,
        text: String,
        phrases: List<String>,
        points: Int,
        title: String,
        detailPrefix: String
    ) {
        val matches = phrases.filter { phrase -> text.contains(phrase) }
        if (matches.isNotEmpty()) {
            breakdown += ScoreBreakdownItem(
                title = title,
                points = points,
                detail = "$detailPrefix: ${matches.joinToString()}."
            )
        }
    }

}
