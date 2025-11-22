package ru.sirius.grable

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.sirius.grable.learn.LearnFragment
import ru.sirius.grable.main.HomeFragment
import ru.sirius.grable.progress.StatsFragment
import ru.sirius.grable.settings.SettingsFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val footerHome = findViewById<View>(R.id.footerHome)
        val footerLearn = findViewById<View>(R.id.footerLearn)
        val footerStats = findViewById<View>(R.id.footerStats)
        val footerSettings = findViewById<View>(R.id.footerSettings)

        footerHome.setOnClickListener { switchFragment(HomeFragment()) }
        footerLearn.setOnClickListener { switchFragment(LearnFragment()) }
        footerStats.setOnClickListener { switchFragment(StatsFragment()) }
        footerSettings.setOnClickListener { switchFragment(SettingsFragment()) }

        if (savedInstanceState == null) {
            switchFragment(HomeFragment())
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}