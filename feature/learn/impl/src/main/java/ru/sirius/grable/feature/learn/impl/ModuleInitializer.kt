package ru.sirius.grable.feature.learn.impl

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.di.AbstractInitializer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.sirius.grable.core.database.WordDao
import ru.sirius.grable.feature.learn.api.Constants
import ru.sirius.grable.feature.learn.api.LearnArgs
import ru.sirius.grable.feature.learn.api.Word
import ru.sirius.grable.feature.learn.impl.domain.LearnPlaylistInteractor
import ru.sirius.grable.feature.learn.impl.domain.WordsRepository
import ru.sirius.grable.feature.learn.impl.ui.LearnFragment
import ru.sirius.grable.feature.learn.impl.ui.LearnPlaylistViewModel
import ru.sirius.grable.navigation.api.FragmentProvider

internal class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(learnModule)
    }
}

val learnModule = module {
    factory<FragmentProvider>(named(Constants.LEARN_SCREEN)) {
        object : FragmentProvider {
            override fun create(arguments: Bundle?): Fragment {
                val playlistId = arguments?.getLong(LearnArgs.PLAYLIST_ID, 1L) ?: 1L
                return LearnFragment.newInstance(playlistId)
            }
        }
    }
    
    // Also register Class for backward compatibility
    factory<Class<out Fragment>>(named(Constants.LEARN_SCREEN)) {
        LearnFragment::class.java
    }
    
    factory<WordsRepository> {
        WordsRepository(get<WordDao>())
    }
    
    factory<LearnPlaylistInteractor> {
        LearnPlaylistInteractor(get<WordsRepository>())
    }
    
    viewModel { (playlistId: Long) ->
        LearnPlaylistViewModel(playlistId)
    }
}

