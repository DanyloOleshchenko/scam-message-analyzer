package com.example.scammessageanalyzer.auth

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    private const val SALT_SIZE = 16
    private const val HASH_SIZE = 32
    private const val ITERATIONS = 120_000
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"

    fun generateSalt(): ByteArray =
        ByteArray(SALT_SIZE).also { SecureRandom().nextBytes(it) }

    fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val keySpec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_SIZE * 8)
        return SecretKeyFactory.getInstance(ALGORITHM)
            .generateSecret(keySpec)
            .encoded
    }

    fun verifyPassword(password: String, salt: ByteArray, expectedHash: ByteArray): Boolean {
        val actualHash = hashPassword(password, salt)
        return MessageDigest.isEqual(actualHash, expectedHash)
    }

    fun encode(bytes: ByteArray): String =
        Base64.encodeToString(bytes, Base64.NO_WRAP)

    fun decode(encodedBytes: String): ByteArray =
        Base64.decode(encodedBytes, Base64.NO_WRAP)
}
