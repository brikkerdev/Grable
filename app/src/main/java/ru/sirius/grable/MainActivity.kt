package ru.sirius.grable

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import ru.sirius.grable.add_word.ui.AddWordFragment
import ru.sirius.grable.databinding.ActivityMainBinding
import ru.sirius.grable.feature.add_word.api.Constants as AddWordConstants
import ru.sirius.grable.feature.learn.api.Constants as LearnConstants
import ru.sirius.grable.feature.progress.api.Constants as ProgressConstants
import ru.sirius.grable.feature.settings.api.Constants as SettingsConstants
import ru.sirius.grable.navigation.api.NavigationRouter
import ru.sirius.grable.navigation.api.Screens

class MainActivity : AppCompatActivity() {
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("app_preferences", MODE_PRIVATE)
    }

    private val navigationRouter: NavigationRouter by inject {
        parametersOf(supportFragmentManager, R.id.fragment_container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val savedThemeMode =
            sharedPreferences.getString("themeId", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString())
        AppCompatDelegate.setDefaultNightMode(savedThemeMode!!.toInt())

        super.onCreate(savedInstanceState)

        // Register NavigationRouter as single so fragments can access it
        // Also register non-modularized fragments in Koin
        getKoin().loadModules(listOf(
            module {
                single<NavigationRouter> {
                    ru.sirius.grable.navigation.impl.NavigationRouter(
                        supportFragmentManager,
                        R.id.fragment_container
                    )
                }

                // Register non-modularized fragments
                factory<Class<out Fragment>>(named(AddWordConstants.ADD_WORD_SCREEN)) {
                    AddWordFragment::class.java
                }                


            }
        ))

        ActivityMainBinding.inflate(layoutInflater).run {
            setContentView(root)

            bottomNav.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        navigationRouter.navigateToScreen(Screens.HOME)
                        true
                    }
                    R.id.nav_add_word -> {
                        navigateToFragment(AddWordConstants.ADD_WORD_SCREEN)
                        true
                    }
                    R.id.nav_learn -> {
                        navigationRouter.navigateToScreen(Screens.LEARN)
                        true
                    }
                    R.id.nav_stats -> {
                        navigationRouter.navigateToScreen(Screens.STATS)
                        true
                    }
                    R.id.nav_settings -> {
                        navigationRouter.navigateToScreen(Screens.SETTINGS)
                        true
                    }
                    else -> false
                }
            }

            if (savedInstanceState == null) {
                navigationRouter.navigateToScreen(Screens.HOME)
                bottomNav.selectedItemId = R.id.nav_home
            }
        }
    }

    fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToFragment(qualifier: String) {
        val fragmentType = get(Class::class.java, named(qualifier)) as? Class<out Fragment>
        fragmentType?.let {
            switchFragment(it.newInstance())
        }
    }
}