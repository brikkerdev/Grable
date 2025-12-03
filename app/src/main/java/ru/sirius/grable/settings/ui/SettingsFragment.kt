package ru.sirius.grable.settings.ui

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
import ru.sirius.grable.settings.domain.SettingsUiState
import ru.sirius.grable.settings.domain.SettingsViewModel
import java.util.Locale

class SettingsFragment : Fragment(), SettingsAdapter.ClickListener {

    private val viewModel: SettingsViewModel by viewModels()
    private val adapter: SettingsAdapter by lazy {
        SettingsAdapter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                updateUI(uiState)
            }
        }
    }

    override fun onClickListener(item: SettingItem) {
        val uiState = viewModel.uiState.value

        when (item.id) {
            R.id.native_language_layout -> {
                showLanguageSelection(uiState)
            }
            R.id.theme_layout -> {
                showThemeSelection(uiState)
            }
            R.id.voice_type_layout -> {
                showVoiceSelection(uiState)
            }
            R.id.about_layout -> {
                showAboutApp(uiState.appVersion)
            }
        }
    }

    override fun onChangeListener(item: SettingItem, value: Boolean) {
        val uiState = viewModel.uiState.value

        when (item.id) {
            R.id.switchReminders -> {
                handleRemindersToggle(value, uiState)
            }
            R.id.switchProgressNotifications -> {
                viewModel.toggleProgressNotifications(value)
            }
        }
    }

    private fun updateUI(uiState: SettingsUiState) {
        val settingsItems = createSettingsItems(uiState)
        adapter.submitList(settingsItems)
    }

    private fun createSettingsItems(uiState: SettingsUiState): List<SettingItem> {
        return listOf(
            // Основные настройки
            SettingItem.SectionTitle(R.id.section_title,"Основные настройки"),
            SettingItem.BaseSetting(
                id = R.id.native_language_layout,
                title = "Родной язык",
                value = uiState.settings.nativeLanguage.name,
                showDivider = true,
            ),
            SettingItem.BaseSetting(
                id = R.id.theme_layout,
                title = "Цветовая тема",
                value = uiState.settings.theme.name,
                showDivider = false,
            ),

            // Аудио настройки
            SettingItem.SectionTitle(R.id.section_title, "Аудио настройки"),
            SettingItem.BaseSetting(
                id = R.id.voice_type_layout,
                title = "Озвучивание диктора",
                value = uiState.settings.voiceType.name,
            ),

            // Уведомления
            SettingItem.SectionTitle(R.id.section_title,"Уведомления"),
            SettingItem.SwitchSetting(
                id = R.id.switchReminders,
                title = "Напоминания",
                subtitle = uiState.settings.reminderTime,
                isChecked = uiState.settings.dailyRemindersEnabled,
                showDivider = true,
            ),
            SettingItem.SwitchSetting(
                id = R.id.switchProgressNotifications,
                title = "Уведомления о прогрессе",
                subtitle = "Еженедельные отчеты о прогрессе",
                isChecked = uiState.settings.progressNotificationsEnabled,
            ),

            // О приложении
            SettingItem.SectionTitle(R.id.section_title,"О приложении"),
            SettingItem.BaseSetting(
                id = R.id.about_layout,
                title = "О приложении",
                value = "",
            ),

            // Версия приложения
            SettingItem.AppVersion(R.id.section_title,"Версия ${uiState.appVersion}")
        )
    }

    private fun showLanguageSelection(uiState: SettingsUiState) {
        val languages = uiState.availableLanguages
        val languageNames = languages.map { it.name }.toTypedArray()
        val currentLanguage = uiState.settings.nativeLanguage
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

    private fun showThemeSelection(uiState: SettingsUiState) {
        val themes = uiState.availableThemes
        val themeNames = themes.map { it.name }.toTypedArray()
        val currentTheme = uiState.settings.theme
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

    private fun showVoiceSelection(uiState: SettingsUiState) {
        val voices = uiState.availableVoices
        val voiceNames = voices.map { it.name }.toTypedArray()
        val currentVoice = uiState.settings.voiceType
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

    private fun handleRemindersToggle(isChecked: Boolean, uiState: SettingsUiState) {
        viewModel.toggleDailyReminders(isChecked)

        if (isChecked) {
            showTimePicker(uiState.settings.reminderTime)
        }
    }

    private fun showTimePicker(currentTime: String) {
        val (hour, minute) = currentTime.split(":").map { it.toInt() }

        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    selectedHour,
                    selectedMinute
                )
                viewModel.updateReminderTime(formattedTime)
            },
            hour,
            minute,
            true
        ).show()
    }

    private fun showAboutApp(appVersion: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("О приложении")
            .setMessage("Grable - приложение для изучения языков\n\nВерсия: $appVersion")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}