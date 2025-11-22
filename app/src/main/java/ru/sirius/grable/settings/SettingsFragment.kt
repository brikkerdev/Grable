package ru.sirius.grable.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import ru.sirius.grable.R

class SettingsFragment : Fragment() {

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val switchTheme = view.findViewById<Switch>(R.id.switchTheme)
        switchTheme?.let {
            setupThemeSwitch(it)
        }
    }

    private fun setupThemeSwitch(switchTheme: Switch) {
        // Получаем текущий режим темы
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val isDarkMode = when (currentMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                // Если следует системной теме, проверяем системную настройку
                val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
                nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
            else -> false
        }
        
        // Устанавливаем начальное состояние переключателя
        switchTheme.isChecked = isDarkMode
        
        // Сохраняем предпочтение пользователя
        val savedThemeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        if (savedThemeMode != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            switchTheme.isChecked = savedThemeMode == AppCompatDelegate.MODE_NIGHT_YES
        }
        
        // Обработчик изменения темы
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            
            // Сохраняем выбор пользователя
            sharedPreferences.edit().putInt("theme_mode", mode).apply()
            
            // Применяем тему
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }
}