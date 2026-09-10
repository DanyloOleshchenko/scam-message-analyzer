package com.example.scammessageanalyzer.auth

import android.content.Context
import android.content.SharedPreferences

class AuthRepository(context: Context) {

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun isPasswordConfigured(): Boolean =
        preferences.contains(KEY_PASSWORD_HASH) && preferences.contains(KEY_PASSWORD_SALT)

    fun savePassword(password: String) {
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword(password, salt)

        preferences.edit()
            .putString(KEY_PASSWORD_SALT, PasswordHasher.encode(salt))
            .putString(KEY_PASSWORD_HASH, PasswordHasher.encode(hash))
            .apply()
    }

    fun verifyPassword(password: String): Boolean {
        val encodedSalt = preferences.getString(KEY_PASSWORD_SALT, null) ?: return false
        val encodedHash = preferences.getString(KEY_PASSWORD_HASH, null) ?: return false

        val salt = PasswordHasher.decode(encodedSalt)
        val expectedHash = PasswordHasher.decode(encodedHash)

        return PasswordHasher.verifyPassword(
            password = password,
            salt = salt,
            expectedHash = expectedHash
        )
    }

    companion object {
        private const val PREFERENCES_NAME = "auth_preferences"
        private const val KEY_PASSWORD_HASH = "password_hash"
        private const val KEY_PASSWORD_SALT = "password_salt"
    }
}
