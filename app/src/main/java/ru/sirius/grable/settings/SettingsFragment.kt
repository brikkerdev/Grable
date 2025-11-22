import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.sirius.grable.R

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFragment(view)
    }

    private fun initFragment(view: View) {
        // Инициализация всех View элементов
        val tvNativeLanguage: TextView = view.findViewById(R.id.tvNativeLanguage)
        val tvVoiceType: TextView = view.findViewById(R.id.tvVoiceType)
        val tvReminderTime: TextView = view.findViewById(R.id.tvReminderTime)
        val switchReminders: Switch = view.findViewById(R.id.switchReminders)
        val switchProgressNotifications: Switch = view.findViewById(R.id.switchProgressNotifications)
        val tvAppVersion: TextView = view.findViewById(R.id.tvAppVersion)

        val nativeLanguageLayout = view.findViewById<LinearLayout>(R.id.native_language_layout)
        val voiceTypeLayout = view.findViewById<LinearLayout>(R.id.voice_type_layout)
        val aboutLayout = view.findViewById<LinearLayout>(R.id.about_layout)
        val reminderTimeLayout = view.findViewById<LinearLayout>(R.id.reminder_time_layout)

        // Настройка наблюдателей
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settingsState.collect { state ->
                    tvNativeLanguage.text = state.nativeLanguage.name
                    tvVoiceType.text = state.voiceType.name
                    tvReminderTime.text = state.reminderTime
                    switchReminders.isChecked = state.dailyRemindersEnabled
                    switchProgressNotifications.isChecked = state.progressNotificationsEnabled
                    tvAppVersion.text = "Версия ${state.appVersion}"
                }
            }
        }

        // Настройка кликов
        nativeLanguageLayout.setOnClickListener { showLanguageSelectionDialog() }
        voiceTypeLayout.setOnClickListener { showVoiceSelectionDialog() }
        reminderTimeLayout.setOnClickListener { showTimePickerDialog() }
        aboutLayout.setOnClickListener { showAboutDialog() }

        switchReminders.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleDailyReminders(isChecked)
        }

        switchProgressNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleProgressNotifications(isChecked)
        }
    }

    // Методы showDialog остаются без изменений
    private fun showLanguageSelectionDialog() {
        val languages = viewModel.availableLanguages
        val currentState = viewModel.settingsState.value
        val currentLanguage = currentState.nativeLanguage

        val items = languages.map { it.name }.toTypedArray()
        val checkedItem = languages.indexOfFirst { it.code == currentLanguage.code }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выберите язык")
            .setSingleChoiceItems(items, checkedItem) { dialog, which ->
                viewModel.updateNativeLanguage(languages[which])
                dialog.dismiss()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showVoiceSelectionDialog() {
        val voices = viewModel.availableVoices
        val currentState = viewModel.settingsState.value
        val currentVoice = currentState.voiceType

        val items = voices.map { it.name }.toTypedArray()
        val checkedItem = voices.indexOfFirst { it.id == currentVoice.id }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выберите тип голоса")
            .setSingleChoiceItems(items, checkedItem) { dialog, which ->
                viewModel.updateVoiceType(voices[which])
                dialog.dismiss()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showTimePickerDialog() {
        val currentTime = viewModel.settingsState.value.reminderTime
        val parts = currentTime.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Выберите время напоминания")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val selectedTime = String.format("%02d:%02d", timePicker.hour, timePicker.minute)
            viewModel.updateReminderTime(selectedTime)
        }

        timePicker.show(parentFragmentManager, "time_picker")
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("О приложении")
            .setMessage("Приложение для изучения языков\n\nВерсия: ${viewModel.getAppVersion()}")
            .setPositiveButton("OK", null)
            .show()
    }
}