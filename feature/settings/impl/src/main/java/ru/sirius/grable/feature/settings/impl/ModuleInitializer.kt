package ru.sirius.grable.feature.settings.impl

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.sirius.di.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.sirius.grable.feature.settings.api.Constants
import ru.sirius.grable.feature.settings.impl.ui.SettingsFragment
import ru.sirius.grable.core.navigation.api.FragmentProvider

internal class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(settingsModule)
    }
}

val settingsModule = module {
    factory<Class<out Fragment>>(named(Constants.SETTINGS_SCREEN)) {
        SettingsFragment::class.java
    }

    factory<FragmentProvider>(named(Constants.SETTINGS_SCREEN)) {
        object : FragmentProvider {
            override fun create(arguments: Bundle?): Fragment {
                return SettingsFragment()
            }
        }
    }
}


