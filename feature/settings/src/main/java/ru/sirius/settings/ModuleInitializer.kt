package ru.sirius.settings

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.di.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.sirius.grable.feature.settings.api.Constants
import ru.sirius.settings.ui.SettingsFragment

internal class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(settingsModule)
    }
}

val settingsModule = module {
    factory<Class<out Fragment>>(named(Constants.SETTINGS_SCREEN)) {
        SettingsFragment::class.java
    }
}


