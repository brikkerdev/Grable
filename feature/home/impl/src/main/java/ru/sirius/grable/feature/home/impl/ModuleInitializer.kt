package ru.sirius.grable.feature.home.impl

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.di.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.sirius.grable.feature.home.api.Constants
import ru.sirius.grable.feature.home.impl.ui.HomeFragment

internal class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(homeModule)
    }
}

val homeModule = module {
    factory<Class<out Fragment>>(named(Constants.HOME_SCREEN)) {
        HomeFragment::class.java
    }
}


