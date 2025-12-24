package ru.sirius.embedded

import android.content.Context
import android.widget.ImageView
import ru.sirius.di.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import ru.sirius.api.ITTS

class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(
            module {
                single<TTSImpl> { TTSImpl(context) }
                single<ITTS> { get<TTSImpl>() }
            }
        )
    }
}