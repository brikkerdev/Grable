// Finally, update MainActivity.kt to handle fragment switching via footer clicks.

package ru.sirius.grable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {

    private lateinit var footerHome: LinearLayout
    private lateinit var footerLearn: LinearLayout
    private lateinit var footerStats: LinearLayout
    private lateinit var footerSettings: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize footer views
        footerHome = findViewById(R.id.footerHome)
        footerLearn = findViewById(R.id.footerLearn)
        footerStats = findViewById(R.id.footerStats)
        footerSettings = findViewById(R.id.footerSettings)

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
            .commit()
    }
}