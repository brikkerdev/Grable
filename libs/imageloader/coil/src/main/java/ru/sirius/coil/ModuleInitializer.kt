package ru.sirius.coil

import android.content.Context
import android.widget.ImageView
import ru.sirius.api.ImageLoader
import ru.sirius.di.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(
            module {
                single<ImageLoader<ImageView>> { CoilImageLoader() }
            }
        )
    }
}