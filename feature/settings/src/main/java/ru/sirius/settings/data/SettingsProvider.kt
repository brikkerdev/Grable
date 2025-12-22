package ru.sirius.settings.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsProvider (
    private val context: Context
) {
    private val sharedPreferences : SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    fun getStringValue(id: String): String {
        return sharedPreferences.getString(id, "NO DATA") ?: "no data"
    }

    fun getBoolValue(id: String): Boolean {
        return sharedPreferences.getBoolean(id, true)
    }
    fun setStringValue(id: String, value: String) {
        sharedPreferences.edit {
            putString(id, value)
        }
    }

    fun setBoolValue(id: String, value: Boolean) {
        sharedPreferences.edit {
            putBoolean(id, value)
        }
    }
}