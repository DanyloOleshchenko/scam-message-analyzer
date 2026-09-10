package com.example.scammessageanalyzer.data.sms

fun buildSmsFingerprint(
    sender: String?,
    body: String,
    receivedAt: Long,
    fallbackId: String? = null
): String {
    val normalizedSender = sender.orEmpty().trim()
    val normalizedBody = body.trim()
    val bodyHash = normalizedBody.hashCode().toUInt().toString(16)

    if (normalizedSender.isNotBlank() || normalizedBody.isNotBlank()) {
        return listOf(
            normalizedSender,
            receivedAt.toString(),
            bodyHash
        ).joinToString("|")
    }

    return fallbackId.orEmpty()
}
