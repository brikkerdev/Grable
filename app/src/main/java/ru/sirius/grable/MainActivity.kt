// Finally, update MainActivity.kt to handle fragment switching via footer clicks.

package ru.sirius.grable

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize footer views
        val footerHome = findViewById<View>(R.id.footerHome)
        val footerLearn = findViewById<View>(R.id.footerLearn)
        val footerStats = findViewById<View>(R.id.footerStats)
        val footerSettings = findViewById<View>(R.id.footerSettings)

        // Set click listeners
        footerHome.setOnClickListener { switchFragment(HomeFragment()) }
        footerLearn.setOnClickListener { switchFragment(LearnFragment()) }
        footerStats.setOnClickListener { switchFragment(StatsFragment()) }
        footerSettings.setOnClickListener { switchFragment(SettingsFragment()) }

        // Load default fragment, e.g., Home
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