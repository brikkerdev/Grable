package ru.sirius.grable.navigation.impl

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.di.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import ru.sirius.grable.navigation.api.NavigationRouter

internal class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(
            module {
                // NavigationRouter будет предоставляться через factory
                // так как требует FragmentManager и containerId
                factory<NavigationRouter> { (fragmentManager: FragmentManager, containerId: Int) ->
                    NavigationRouter(fragmentManager, containerId)
                }
            }
        )
    }
}

