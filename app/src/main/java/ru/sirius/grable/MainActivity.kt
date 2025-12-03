package ru.sirius.grable

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import ru.sirius.grable.learn.LearnFragment
import ru.sirius.grable.main.HomeFragment
import ru.sirius.grable.progress.StatsFragment
import ru.sirius.grable.settings.ui.SettingsFragment
import ru.sirius.grable.add_word.ui.AddWordFragment

class MainActivity : AppCompatActivity() {

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val savedThemeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedThemeMode)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val footerHome = findViewById<View>(R.id.footerHome)
        val footerLearn = findViewById<View>(R.id.footerLearn)
        val footerAddWord = findViewById<View>(R.id.footerAddWord)
        val footerStats = findViewById<View>(R.id.footerStats)
        val footerSettings = findViewById<View>(R.id.footerSettings)

        footerHome.setOnClickListener { switchFragment(HomeFragment()) }
        footerLearn.setOnClickListener { switchFragment(LearnFragment()) }
        footerAddWord.setOnClickListener { switchFragment(AddWordFragment()) }
        footerStats.setOnClickListener { switchFragment(StatsFragment()) }
        footerSettings.setOnClickListener { switchFragment(SettingsFragment()) }

        if (savedInstanceState == null) {
            switchFragment(HomeFragment())
        }
    }

    fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}