package ru.sirius.grable.settings

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.sirius.grable.R
import java.util.Locale

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    // Используем ленивую инициализацию для адаптера
    private val adapter: SettingsAdapter by lazy {
        val initialSettingsState = viewModel.settingsState.value
        val settingsItems = createSettingsItems(initialSettingsState)
        SettingsAdapter(settingsItems)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем RecyclerView напрямую из view
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Наблюдаем за изменениями состояния настроек
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.settingsState.collect { settingsState ->
                updateAdapterData(settingsState)
            }
        }
    }

    private fun createSettingsItems(settingsState: SettingsState): List<SettingItem> {
        return listOf(
            // Основные настройки
            SettingItem.SectionTitle("Основные настройки"),
            SettingItem.BaseSetting(
                id = R.id.native_language_layout,
                title = "Родной язык",
                value = settingsState.nativeLanguage.name,
                showDivider = true,
                onClick = { showLanguageSelection() }
            ),
            SettingItem.BaseSetting(
                id = R.id.theme_layout,
                title = "Цветовая тема",
                value = settingsState.theme.name,
                showDivider = false,
                onClick = { showThemeSelection() }
            ),

            // Аудио настройки
            SettingItem.SectionTitle("Аудио настройки"),
            SettingItem.BaseSetting(
                id = R.id.voice_type_layout,
                title = "Озвучивание диктора",
                value = settingsState.voiceType.name,
                onClick = { showVoiceSelection() }
            ),

            // Уведомления
            SettingItem.SectionTitle("Уведомления"),
            SettingItem.SwitchSetting(
                id = R.id.switchReminders,
                title = "Напоминания",
                subtitle = settingsState.reminderTime,
                isChecked = settingsState.dailyRemindersEnabled,
                showDivider = true,
                onCheckedChange = { isChecked ->
                    handleRemindersToggle(isChecked)
                }
            ),
            SettingItem.SwitchSetting(
                id = R.id.switchProgressNotifications,
                title = "Уведомления о прогрессе",
                subtitle = "Еженедельные отчеты о прогрессе",
                isChecked = settingsState.progressNotificationsEnabled,
                onCheckedChange = { isChecked ->
                    handleProgressNotificationsToggle(isChecked)
                }
            ),

            // О приложении
            SettingItem.SectionTitle("О приложении"),
            SettingItem.BaseSetting(
                id = R.id.about_layout,
                title = "О приложении",
                value = "",
                onClick = { showAboutApp() }
            ),

            // Версия приложения
            SettingItem.AppVersion("Версия ${viewModel.getAppVersion()}")
        )
    }

    private fun updateAdapterData(settingsState: SettingsState) {
        val newSettingsItems = createSettingsItems(settingsState)
        adapter.updateItems(newSettingsItems)
    }

    private fun showLanguageSelection() {
        val languages = viewModel.availableLanguages
        val languageNames = languages.map { it.name }.toTypedArray()
        val currentLanguage = viewModel.settingsState.value.nativeLanguage
        val currentLanguageIndex = languages.indexOfFirst { it.code == currentLanguage.code }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выберите язык")
            .setSingleChoiceItems(languageNames, currentLanguageIndex) { dialog, which ->
                val selectedLanguage = languages[which]
                viewModel.updateNativeLanguage(selectedLanguage)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showThemeSelection() {
        val themes = viewModel.availableThemes
        val themeNames = themes.map { it.name }.toTypedArray()
        val currentTheme = viewModel.settingsState.value.theme
        val currentThemeIndex = themes.indexOfFirst { it.id == currentTheme.id }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выберите тему")
            .setSingleChoiceItems(themeNames, currentThemeIndex) { dialog, which ->
                val selectedTheme = themes[which]
                viewModel.updateTheme(selectedTheme)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showVoiceSelection() {
        val voices = viewModel.availableVoices
        val voiceNames = voices.map { it.name }.toTypedArray()
        val currentVoice = viewModel.settingsState.value.voiceType
        val currentVoiceIndex = voices.indexOfFirst { it.id == currentVoice.id }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выберите голос диктора")
            .setSingleChoiceItems(voiceNames, currentVoiceIndex) { dialog, which ->
                val selectedVoice = voices[which]
                viewModel.updateVoiceType(selectedVoice)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handleRemindersToggle(isChecked: Boolean) {
        viewModel.toggleDailyReminders(isChecked)

        if (isChecked) {
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        val currentTime = viewModel.settingsState.value.reminderTime
        val (hour, minute) = currentTime.split(":").map { it.toInt() }

        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                viewModel.updateReminderTime(formattedTime)
            },
            hour,
            minute,
            true
        ).show()
    }

    private fun handleProgressNotificationsToggle(isChecked: Boolean) {
        viewModel.toggleProgressNotifications(isChecked)
    }

    private fun showAboutApp() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("О приложении")
            .setMessage("Grable - приложение для изучения языков\n\nВерсия: ${viewModel.getAppVersion()}")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}