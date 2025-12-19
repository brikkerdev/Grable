package ru.sirius.grable.settings.ui

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
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
import ru.sirius.grable.settings.data.ID_DAILY_REMINDER
import ru.sirius.grable.settings.data.ID_LANGUAGE
import ru.sirius.grable.settings.data.ID_NOTIFICATION_PROGRESS
import ru.sirius.grable.settings.data.ID_THEME
import ru.sirius.grable.settings.data.ID_TIME_REMINDER
import ru.sirius.grable.settings.data.ID_VOICE
import ru.sirius.grable.settings.data.SettingValues
import ru.sirius.grable.settings.data.SettingsProvider
import ru.sirius.grable.settings.domain.SettingsUIState
import ru.sirius.grable.settings.domain.SettingsViewModel
import java.util.Locale

class SettingsFragment : Fragment(), SettingsAdapter.ClickListener {
    private val viewModel: SettingsViewModel by viewModels { SettingsViewModel.Factory }
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
        recyclerView.itemAnimator = null

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect(::updateUI)
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
                handleProgressNotificatoins(value, uiState)
            }
        }
    }

    private fun updateUI(uiState: SettingsUIState) {
        val settingsItems = settingsItemsFactory.createSettingsItems(uiState)
        adapter.submitList(settingsItems)
    }

    private fun handleProgressNotificatoins(value: Boolean, uiState: SettingsUIState) {
        val selected = SettingValues.BooleanValue(ID_NOTIFICATION_PROGRESS, value)
        viewModel.update(selected)
    }
    private fun showLanguageSelection(uiState: SettingsUIState) {
        val languages = uiState.availableLanguages
        val languageNames = languages.values.toTypedArray()
        val currentLanguage = uiState.values[ID_LANGUAGE]?.id ?: 0
        val currentLanguageIndex = languages.keys.indexOfFirst { it == currentLanguage }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выберите язык")
            .setSingleChoiceItems(languageNames, currentLanguageIndex) { dialog, which ->
                val selected = SettingValues.StringValue(ID_LANGUAGE, languageNames[which])
                viewModel.update(selected)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showThemeSelection(uiState: SettingsUIState) {
        val themes = uiState.availableThemes
        val themeNames = themes.values.toTypedArray()
        val currentTheme = uiState.values[ID_THEME]?.id ?: 0
        val currentThemeIndex = themes.keys.indexOfFirst { it == currentTheme }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выберите тему")
            .setSingleChoiceItems(themeNames, currentThemeIndex) { dialog, which ->
                val selected = SettingValues.StringValue(ID_THEME, themeNames[which])
                viewModel.update(selected)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showVoiceSelection(uiState: SettingsUIState) {
        val voices = uiState.availableVoices
        val voiceNames = voices.values.toTypedArray()
        val currentVoice = uiState.values[ID_VOICE]?.id ?: 0
        val currentVoiceIndex = voices.keys.indexOfFirst { it == currentVoice }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_dialog_voice_type))
            .setSingleChoiceItems(voiceNames, currentVoiceIndex) { dialog, which ->
                val selected = SettingValues.StringValue(ID_VOICE, voiceNames[which])
                viewModel.update(selected)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.settings_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handleRemindersToggle(isChecked: Boolean, uiState: SettingsUIState) {
        val selected = SettingValues.BooleanValue(ID_DAILY_REMINDER, isChecked)
        viewModel.update(selected)

        if (isChecked) {
            showTimePicker(uiState.values[ID_TIME_REMINDER]?.stringValue() ?: "19:00")
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
                val selected = SettingValues.StringValue(ID_TIME_REMINDER, formattedTime)
                viewModel.update(selected)
            },
            hour,
            minute,
            true
        ).show()
    }

    private fun showAboutApp(uiState: SettingsUIState) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_about))
            .setMessage(getString(R.string.settings_about_text))
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}