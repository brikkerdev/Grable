package ru.sirius.grable.feature.settings.impl.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import ru.sirius.feature.settings.impl.R
import ru.sirius.grable.feature.settings.impl.databinding.FragmentSettingsBinding
import ru.sirius.grable.feature.settings.impl.data.ID_DAILY_REMINDER
import ru.sirius.grable.feature.settings.impl.data.ID_LANGUAGE
import ru.sirius.grable.feature.settings.impl.data.ID_NOTIFICATION_PROGRESS
import ru.sirius.grable.feature.settings.impl.data.ID_THEME
import ru.sirius.grable.feature.settings.impl.data.ID_TIME_REMINDER
import ru.sirius.grable.feature.settings.impl.data.ID_VOICE
import ru.sirius.grable.feature.settings.impl.data.SettingValues
import ru.sirius.grable.feature.settings.impl.domain.SettingsUIState
import java.util.Locale

class SettingsFragment : Fragment(), SettingsAdapter.ClickListener {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels { SettingsViewModel.Factory }
    private val adapter: SettingsAdapter by lazy {
        SettingsAdapter(this)
    }
    private val settingsItemsFactory = SettingsItemsFactory()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.Companion.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
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
        val languageIds = languages.keys.toTypedArray()
        val currentLanguage = uiState.values[ID_LANGUAGE]?.id ?: 0
        val currentLanguageIndex = languages.keys.indexOfFirst { it == currentLanguage }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_choose_language))
            .setSingleChoiceItems(languageNames, currentLanguageIndex) { dialog, which ->
                val selected = SettingValues.StringValue(ID_LANGUAGE, languageIds[which])
                viewModel.update(selected)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.create(Locale.forLanguageTag(languageIds[which]))
                )
                Log.d("LANG", languageIds[which])
                dialog.dismiss()
            }
            .setNegativeButton(R.string.settings_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showThemeSelection(uiState: SettingsUIState) {
        val themes = uiState.availableThemes
        val themeNames = themes.values.toTypedArray()
        val themeIds = themes.keys.toTypedArray()
        val currentTheme = uiState.values[ID_THEME]?.value ?: -1
        val currentThemeIndex = themes.keys.indexOfFirst { it == currentTheme }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_choose_theme))
            .setSingleChoiceItems(themeNames, currentThemeIndex) { dialog, which ->
                val selected = SettingValues.StringValue(ID_THEME, themeIds[which])
                viewModel.update(selected)
                AppCompatDelegate.setDefaultNightMode(themeIds[which].toInt())
                dialog.dismiss()
            }
            .setNegativeButton(R.string.settings_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showVoiceSelection(uiState: SettingsUIState) {
        val voices = uiState.availableVoices
        val voiceNames = voices.values.toTypedArray()
        val voiceIds = voices.keys.toTypedArray()
        val currentVoice = uiState.values[ID_VOICE]?.id ?: 0
        val currentVoiceIndex = voices.keys.indexOfFirst { it == currentVoice }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_dialog_voice_type))
            .setSingleChoiceItems(voiceNames, currentVoiceIndex) { dialog, which ->
                val selected = SettingValues.StringValue(ID_VOICE, voiceIds[which])
                viewModel.update(selected)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.settings_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handleRemindersToggle(isChecked: Boolean, uiState: SettingsUIState) {
        if (uiState.values[ID_DAILY_REMINDER]?.booleanValue() == isChecked) {
            return
        }
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
            .setPositiveButton(getString(R.string.settings_ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}