package ru.sirius.grable.feature.home.impl

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.di.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.sirius.grable.feature.home.api.Constants
import ru.sirius.grable.feature.home.impl.ui.HomeFragment
import ru.sirius.grable.navigation.api.FragmentProvider

internal class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(homeModule)
    }
}

val homeModule = module {
    factory<FragmentProvider>(named(Constants.HOME_SCREEN)) {
        object : FragmentProvider {
            override fun create(arguments: Bundle?): Fragment {
                return HomeFragment()
            }
        }
    }
    
    factory<Class<out Fragment>>(named(Constants.HOME_SCREEN)) {
        HomeFragment::class.java
    }
}


