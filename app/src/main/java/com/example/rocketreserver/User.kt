package com.example.rocketreserver

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object User {
    private const val KEY_TOKEN = "TOKEN"
    private fun preferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getToken(context: Context): String? {
        return preferences(context).getString(KEY_TOKEN, null)
    }

    fun setToken(context: Context, token: String) {
        preferences(context).edit().apply {
            putString(KEY_TOKEN, token)
            apply()
        }
    }

    fun removeToken(context: Context) {
        preferences(context).edit().apply {
            remove(KEY_TOKEN)
            apply()
        }
    }
}