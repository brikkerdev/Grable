package ru.sirius.grable

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import ru.sirius.grable.databinding.ActivityMainBinding
import ru.sirius.grable.core.navigation.api.NavigationRouter
import ru.sirius.grable.core.navigation.api.Screens
import ru.sirius.grable.core.database.DataSyncService
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("app_preferences", MODE_PRIVATE)
    }

    private val navigationRouter: NavigationRouter by inject {
        parametersOf(supportFragmentManager, R.id.fragment_container)
    }

    private val dataSyncService: DataSyncService by inject()
    
    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        val savedThemeMode = sharedPreferences.getString(
            "themeId",
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()
        )
        val themeMode = try {
            savedThemeMode?.toInt() ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        } catch (e: NumberFormatException) {
            // Если сохранено строковое значение (например, "light", "dark"), используем значение по умолчанию
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        val savedLang =
            sharedPreferences.getString(
                "nativeLanguageId", "en"
            ) ?: "en"

        AppCompatDelegate.setDefaultNightMode(themeMode)
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.create(Locale.forLanguageTag(savedLang))
        )

        super.onCreate(savedInstanceState)

        getKoin().loadModules(
            listOf(
            module {
                single<NavigationRouter> {
                    ru.sirius.grable.core.navigation.impl.NavigationRouter(
                        supportFragmentManager,
                        R.id.fragment_container
                    )
                }


            }
        ))

        ActivityMainBinding.inflate(layoutInflater).run {
            setContentView(root)

            val itemSelectedListener = com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        navigationRouter.navigateToScreen(Screens.HOME)
                        true
                    }

                    R.id.nav_add_word -> {
                        navigationRouter.navigateToScreen(Screens.ADD_WORD)
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
            
            bottomNav.setOnItemSelectedListener(itemSelectedListener)

            navigationRouter.setNavigationListener { screen ->
                val menuItemId = when (screen) {
                    Screens.HOME -> R.id.nav_home
                    Screens.ADD_WORD -> R.id.nav_add_word
                    Screens.LEARN -> R.id.nav_learn
                    Screens.STATS -> R.id.nav_stats
                    Screens.SETTINGS -> R.id.nav_settings
                    else -> null
                }
                menuItemId?.let {
                    if (bottomNav.selectedItemId != it) {
                        bottomNav.setOnItemSelectedListener(null)
                        bottomNav.selectedItemId = it
                        bottomNav.setOnItemSelectedListener(itemSelectedListener)
                    }
                }
            }

            if (savedInstanceState == null) {
                // Устанавливаем selectedItemId перед навигацией, отключая listener, чтобы избежать двойного вызова
                bottomNav.setOnItemSelectedListener(null)
                bottomNav.selectedItemId = R.id.nav_home
                bottomNav.setOnItemSelectedListener(itemSelectedListener)
                navigationRouter.navigateToScreen(Screens.HOME)
            }
        }
        
        // Синхронизируем данные из API при первом запуске
        syncDataIfNeeded()
    }
    
    private fun syncDataIfNeeded() {
        activityScope.launch(Dispatchers.IO) {
            try {
                if (!dataSyncService.hasData()) {
                    Log.d("MainActivity", "No data in database, syncing from API")
                    dataSyncService.syncDataFromApi()
                } else {
                    Log.d("MainActivity", "Data already exists in database")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error syncing data", e)
            }
        }
    }
}