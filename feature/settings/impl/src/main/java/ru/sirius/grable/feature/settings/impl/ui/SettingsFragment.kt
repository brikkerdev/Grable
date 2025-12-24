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
import org.koin.android.ext.android.inject
import ru.sirius.api.ITTS
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
    private val tts: ITTS by inject()

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
            viewModel.uiState.collect { uiState ->
                updateUI(uiState)
                // Применяем текущий голос при загрузке настроек
                val voiceId = uiState.values[ID_VOICE]?.stringValue() ?: "female"
                tts.setVoice(voiceId)
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
                handleProgressNotificatoins(value, uiState)
            }
        }
    }

    private fun updateUI(uiState: SettingsUIState) {
        val langId = uiState.values[ID_LANGUAGE]?.stringValue() ?: "ru"
        val voiceId = uiState.values[ID_VOICE]?.stringValue() ?: "female"
        val themeId = uiState.values[ID_THEME]?.stringValue()
            ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()

        val settingsItems = settingsItemsFactory.createSettingsItems(
            uiState = uiState,
            languageValue = languageLabel(langId),
            voiceValue = voiceLabel(voiceId),
            themeValue = themeLabel(themeId),
        )
        adapter.submitList(settingsItems)
    }

    private fun handleProgressNotificatoins(value: Boolean, uiState: SettingsUIState) {
        val selected = SettingValues.BooleanValue(ID_NOTIFICATION_PROGRESS, value)
        viewModel.update(selected)
    }

    private fun showLanguageSelection(uiState: SettingsUIState) {
        val ids = uiState.availableLanguageIds.toTypedArray()
        val names = ids.map(::languageLabel).toTypedArray()

        val current = uiState.values[ID_LANGUAGE]?.stringValue() ?: "ru"
        val checked = ids.indexOfFirst { it == current }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_choose_language))
            .setSingleChoiceItems(names, checked) { dialog, which ->
                val lang = ids[which]
                viewModel.update(SettingValues.StringValue(ID_LANGUAGE, lang))
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang))
                dialog.dismiss()
            }
            .setNegativeButton(R.string.settings_cancel) { d, _ -> d.dismiss() }
            .show()
    }

    private fun showThemeSelection(uiState: SettingsUIState) {
        val ids = uiState.availableThemeIds.toTypedArray()
        val names = ids.map(::themeLabel).toTypedArray()

        val current = uiState.values[ID_THEME]?.stringValue()
            ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()
        val checked = ids.indexOfFirst { it == current }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_choose_theme))
            .setSingleChoiceItems(names, checked) { dialog, which ->
                val mode = ids[which]
                viewModel.update(SettingValues.StringValue(ID_THEME, mode))
                AppCompatDelegate.setDefaultNightMode(mode.toInt())
                dialog.dismiss()
            }
            .setNegativeButton(R.string.settings_cancel) { d, _ -> d.dismiss() }
            .show()
    }

    private fun showVoiceSelection(uiState: SettingsUIState) {
        val ids = uiState.availableVoiceIds.toTypedArray()
        val names = ids.map(::voiceLabel).toTypedArray()

        val current = uiState.values[ID_VOICE]?.stringValue() ?: "female"
        val checked = ids.indexOfFirst { it == current }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_dialog_voice_type))
            .setSingleChoiceItems(names, checked) { dialog, which ->
                val voice = ids[which]
                viewModel.update(SettingValues.StringValue(ID_VOICE, voice))
                // Обновляем голос TTS
                tts.setVoice(voice)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.settings_cancel) { d, _ -> d.dismiss() }
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

    private fun languageLabel(id: String) = when (id) {
        "ru" -> getString(R.string.caption_lang_ru)
        "en" -> getString(R.string.caption_lang_en)
        else -> id
    }

    private fun voiceLabel(id: String) = when (id) {
        "male" -> getString(R.string.settings_voice_male)
        "female" -> getString(R.string.settings_voice_female)
        else -> id
    }

    private fun themeLabel(id: String) = when (id) {
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString() -> getString(R.string.settings_theme_system)
        AppCompatDelegate.MODE_NIGHT_NO.toString() -> getString(R.string.settings_theme_light)
        AppCompatDelegate.MODE_NIGHT_YES.toString() -> getString(R.string.settings_theme_dark)
        else -> id
    }
}