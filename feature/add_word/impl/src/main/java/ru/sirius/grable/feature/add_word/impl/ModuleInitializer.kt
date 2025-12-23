package ru.sirius.grable.feature.add_word.impl

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.di.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.sirius.grable.feature.add_word.api.Constants
import ru.sirius.grable.feature.add_word.impl.ui.AddWordFragment
import ru.sirius.grable.navigation.api.FragmentProvider

internal class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(addWordModule)
    }
}

val addWordModule = module {
    factory<FragmentProvider>(named(Constants.ADD_WORD_SCREEN)) {
        object : FragmentProvider {
            override fun create(arguments: Bundle?): Fragment {
                return AddWordFragment()
            }
        }
    }
    
    factory<Class<out Fragment>>(named(Constants.ADD_WORD_SCREEN)) {
        AddWordFragment::class.java
    }
}

