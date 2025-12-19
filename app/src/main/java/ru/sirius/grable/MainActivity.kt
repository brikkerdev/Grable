package ru.sirius.grable

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import ru.sirius.grable.learn.ui.LearnFragment
import ru.sirius.grable.main.HomeFragment
import ru.sirius.grable.progress.StatsFragment
import ru.sirius.grable.settings.ui.SettingsFragment
import ru.sirius.grable.add_word.ui.AddWordFragment
import ru.sirius.grable.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val savedThemeMode =
            sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedThemeMode)

        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).run {
            setContentView(root)
            footer.footerHome.setOnClickListener {
                switchFragment(HomeFragment())
            }
            footer.footerLearn.setOnClickListener { switchFragment(LearnFragment()) }
            footer.footerAddWord.setOnClickListener { switchFragment(AddWordFragment()) }
            footer.footerStats.setOnClickListener { switchFragment(StatsFragment()) }
            footer.footerSettings.setOnClickListener { switchFragment(SettingsFragment()) }

            if (savedInstanceState == null) {
                switchFragment(HomeFragment())
            }
        }
    }

    fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}