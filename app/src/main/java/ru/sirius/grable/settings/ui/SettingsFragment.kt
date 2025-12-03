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
    private val settingsItemsFactory = SettingsItemsFactory()

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
                showAboutApp(uiState)
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
        val settingsItems = settingsItemsFactory.createSettingsItems(uiState)
        adapter.submitList(settingsItems)
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

    private fun showAboutApp(uiState: SettingsUiState) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(uiState.texts.aboutAppTitle)
            .setMessage("Grable - приложение для изучения языков\n\n${uiState.texts.aboutAppSubtitle}")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}