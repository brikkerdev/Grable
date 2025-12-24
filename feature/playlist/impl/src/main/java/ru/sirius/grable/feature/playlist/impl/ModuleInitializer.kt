package ru.sirius.grable.feature.playlist.impl

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.sirius.di.AbstractInitializer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.sirius.grable.core.database.PlaylistDao
import ru.sirius.grable.core.database.WordDao
import ru.sirius.grable.feature.playlist.api.Constants
import ru.sirius.grable.feature.playlist.impl.domain.SelectPlaylistInteractor
import ru.sirius.grable.feature.playlist.impl.domain.SelectPlaylistRepository
import ru.sirius.grable.feature.playlist.impl.domain.SelectPlaylistRepositoryImpl
import ru.sirius.grable.feature.playlist.impl.ui.SelectPlaylistFragment
import ru.sirius.grable.core.navigation.api.FragmentProvider

internal class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(playlistModule)
    }
}

val playlistModule = module {
    factory<FragmentProvider>(named(Constants.SELECT_PLAYLIST_SCREEN)) {
        object : FragmentProvider {
            override fun create(arguments: Bundle?): Fragment {
                return SelectPlaylistFragment()
            }
        }
    }

    factory<Class<out Fragment>>(named(Constants.SELECT_PLAYLIST_SCREEN)) {
        SelectPlaylistFragment::class.java
    }

    factory<SelectPlaylistRepository> {
        SelectPlaylistRepositoryImpl(get<PlaylistDao>())
    }

    factory<SelectPlaylistInteractor> {
        SelectPlaylistInteractor(get<SelectPlaylistRepository>())
    }
}