package ru.sirius.grable.feature.settings.impl.data

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

    fun getIntValue(id: String, defaultValue: Int = 10): Int {
        return sharedPreferences.getInt(id, defaultValue)
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

    fun setIntValue(id: String, value: Int) {
        sharedPreferences.edit {
            putInt(id, value)
        }
    }

    fun getLongListValue(id: String): List<Long> {
        val json = sharedPreferences.getString(id, null) ?: return emptyList()
        return try {
            json.split(",").mapNotNull { it.trim().toLongOrNull() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun setLongListValue(id: String, value: List<Long>) {
        val json = value.joinToString(",")
        sharedPreferences.edit {
            putString(id, json)
        }
    }
}