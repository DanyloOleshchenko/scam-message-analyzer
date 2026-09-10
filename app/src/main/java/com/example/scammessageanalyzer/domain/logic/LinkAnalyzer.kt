package com.example.scammessageanalyzer.domain.logic

import com.example.scammessageanalyzer.domain.model.LinkAnalysisResult
import java.net.URI

object LinkAnalyzer {

    private val urlRegex = Regex(
        pattern = """(?i)(?<!@)\b((?:https?://|www\.)[^\s<>()]+|(?:[a-z0-9-]+\.)+[a-z]{2,}(?:/[^\s<>()]*)?)""",
        options = setOf(RegexOption.IGNORE_CASE)
    )

    private val shortenerHosts = setOf(
        "bit.ly", "tinyurl.com", "goo.gl", "t.co", "ow.ly", "is.gd", "buff.ly", "cutt.ly"
    )

    private val highRiskTlds = setOf(
        "zip", "top", "click", "gq", "work", "rest", "cam", "mom", "country"
    )

    private val suspiciousPathKeywords = listOf(
        "login", "verify", "secure", "update", "confirm", "claim", "gift", "reward",
        "wallet", "payment", "pay", "signin", "suspend", "unlock", "account"
    )

    private val ipv4Regex = Regex("""^\d{1,3}(\.\d{1,3}){3}$""")

    fun analyzeText(text: String): List<LinkAnalysisResult> =
        extractUrls(text).map(::analyzeUrl)

    private fun extractUrls(text: String): List<String> =
        urlRegex.findAll(text)
            .map { match -> trimTrailingPunctuation(match.value) }
            .filter { it.contains('.') }
            .distinct()
            .toList()

    private fun analyzeUrl(url: String): LinkAnalysisResult {
        val normalizedUrl = if (url.contains("://")) url else "https://$url"
        val uri = runCatching { URI(normalizedUrl) }.getOrNull()
        val host = uri?.host?.lowercase()?.removePrefix("www.").orEmpty()
        val findings = mutableListOf<String>()
        var score = 0

        if (normalizedUrl.startsWith("http://", ignoreCase = true)) {
            score += 10
            findings += "Uses insecure HTTP instead of HTTPS."
        }

        if (host in shortenerHosts) {
            score += 18
            findings += "Uses a URL shortener that hides the real destination."
        }

        if (host.matches(ipv4Regex)) {
            score += 16
            findings += "Uses a raw IP address instead of a normal domain name."
        }

        if (host.contains("xn--")) {
            score += 15
            findings += "Contains punycode, which can disguise a lookalike domain."
        }

        val hostParts = host.split('.').filter { it.isNotBlank() }
        if (hostParts.size >= 4) {
            score += 7
            findings += "Has many subdomains, which can hide the real website."
        }

        val tld = host.substringAfterLast('.', "")
        if (tld in highRiskTlds) {
            score += 10
            findings += "Uses a higher-risk top-level domain .$tld."
        }

        val hostDigits = host.count(Char::isDigit)
        val hostHyphens = host.count { it == '-' }
        if (hostDigits >= 6 || hostHyphens >= 3) {
            score += 7
            findings += "Domain looks obfuscated with many digits or hyphens."
        }

        if (url.contains("@")) {
            score += 15
            findings += "Contains '@', which can hide the true destination."
        }

        val rawLowerUrl = url.lowercase()
        if (suspiciousPathKeywords.any { keyword ->
                rawLowerUrl.contains("/$keyword") ||
                    rawLowerUrl.contains("$keyword=") ||
                    rawLowerUrl.contains("$keyword-")
            }
        ) {
            score += 8
            findings += "Path or query pushes login, verification, payment, or account action."
        }

        val queryParamCount = uri?.rawQuery
            ?.split("&")
            ?.count { it.isNotBlank() }
            ?: 0
        if (queryParamCount >= 4) {
            score += 6
            findings += "Includes many query parameters, which often appear in redirects or tracking links."
        }

        score = score.coerceAtMost(40)
        val riskLevel = when {
            score >= 25 -> "High Risk Link"
            score >= 10 -> "Suspicious Link"
            else -> "Looks Normal"
        }

        return LinkAnalysisResult(
            originalUrl = url,
            displayHost = host.ifBlank { url },
            riskLevel = riskLevel,
            riskScore = score,
            findings = findings
        )
    }

    private fun trimTrailingPunctuation(value: String): String =
        value.trimEnd('.', ',', '!', '?', ';', ':', ')', ']', '}')
}
